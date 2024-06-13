package ru.itmo.client.main;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.client.core.Console;
import ru.itmo.client.core.UDPConnector;
import ru.itmo.client.core.UDPReader;
import ru.itmo.client.core.UDPSender;
import ru.itmo.common.commands.*;
import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.exceptions.CommandIOException;
import ru.itmo.common.interfaces.Printer;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.models.StudyGroup;
import ru.itmo.common.utility.IOProvider;
import ru.itmo.common.utility.SGParser;
import ru.itmo.common.utility.StandardPrinter;
import ru.itmo.common.interfaces.Accessible;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);  // Логгер для логирования событий
    private static UDPSender sender;  // Отправитель UDP-сообщений
    private static UDPReader reader;  // Читатель UDP-сообщений
    @Setter @Getter
    private String login;  // Логин пользователя
    @Setter @Getter
    private String password;  // Пароль пользователя
    private IOProvider provider;  // Провайдер ввода-вывода
    private CommandManager commandManager;  // Менеджер команд
    private UDPConnector connector;  // Соединитель для UDP
    private Console console;  // Консоль для взаимодействия с пользователем

    // Конструктор класса Runner
    public Runner() {
        this.provider = initializeIOProvider();
        this.commandManager = initializeCommandManager(provider);
        this.connector = initializeUDPConnector();
        this.connect();
    }

    // Метод для установления соединения с сервером
    public boolean connect() {
        logger.info("Starting console and connecting to server...");
        int port = 3940;  // Порт для соединения
        if (connector.connect()) {
            reader = new UDPReader(connector.getDatagramSocket());
            sender = new UDPSender(connector.getDatagramSocket(), connector.getServerAddress(), port, reader);
            return true;
        } else {
            logger.error("Failed to connect to the server.");
            return false;
        }
    }

    // Инициализация провайдера ввода-вывода
    private IOProvider initializeIOProvider() {
        Scanner scanner = new Scanner(System.in);
        Printer printer = new StandardPrinter();
        return new IOProvider(scanner, printer);
    }

    // Инициализация менеджера команд
    private CommandManager initializeCommandManager(IOProvider provider) {
        CommandManager commandManager = new CommandManager(provider);
        String[] commandNames = {"help", "info", "show", "add", "update", "remove_by_id", "clear", "save",
                "execute_script", "exit", "add_if_min", "count_less_than_group_admin",
                "remove_first", "login", "register"};
        Command[] commands = {new Help(), new Info(), new Show(), new Add(), new Update(),
                new RemoveById(), new Clear(), new ExecuteScript(), new Exit(),
                new AddIfMin(), new CountLesAdminName(),  new RemoveFirst(), new LogIn(), new Register()};

        for (int i = 0; i < commands.length; i++) {
            try {
                commandManager.createCommand(commandNames[i], commands[i]);
            } catch (CommandIOException e) {
                provider.getPrinter().print(e.getMessage());
            }
        }
        return commandManager;
    }

    // Инициализация UDP соединителя
    private UDPConnector initializeUDPConnector() {
        return new UDPConnector("localhost", 1234);
    }

    // Отправка команды на сервер
    public Response sendShallow(CommandShallow shallow) throws IOException {
        return sender.executeCommand(shallow);
    }

    // Получение списка StudyGroup с сервера
    public List<StudyGroup> fetchStudyGroups() {
        CommandShallow shallow = new CommandShallow("show", null, null, login, password);
        try {
            Response response = sendShallow(shallow);
            System.out.println(response);
            return (List<StudyGroup>) response.getData();
        } catch (IOException e) {
            logger.error("Fetching study groups failed", e);
            return null;
        }
    }

    // Обновление StudyGroup на сервере
    public void updateStudyGroup(StudyGroup studyGroup) {
        CommandShallow shallow = new CommandShallow("update", studyGroup.getId(), studyGroup, login, password);
        try {
            sendShallow(shallow);
        } catch (IOException e) {
            logger.error("Updating study group failed", e);
        }
    }

    // Удаление StudyGroup с сервера
    public void deleteStudyGroup(StudyGroup studyGroup) {
        CommandShallow shallow = new CommandShallow("remove_by_id", new String[]{String.valueOf(studyGroup.getId())}, null, login, password);
        try {
            sendShallow(shallow);
        } catch (IOException e) {
            logger.error("Deleting study group failed", e);
        }
    }

    // Очистка всех StudyGroup на сервере
    public boolean clearStudyGroups() {
        CommandShallow shallow = new CommandShallow("clear", null, null, login, password);
        try {
            Response response = sendShallow(shallow);
            return response.isSuccess();
        } catch (IOException e) {
            logger.error("Clearing study groups failed", e);
            return false;
        }
    }

    // Выполнение скрипта
    public Runner.ExitCode scriptMode(File file) {
        logger.info("Script mode: " + file.getAbsolutePath());
        if (!file.exists()) {
            return ExitCode.ERROR;
        }

        try (Scanner scriptScanner = new Scanner(file)) {
            if (!scriptScanner.hasNext()) {
                return ExitCode.ERROR;
            }

            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] commandParts = line.split("\\s+", 2);
                String commandName = commandParts[0];
                String commandArgument = commandParts.length > 1 ? commandParts[1].trim() : null;
                CommandShallow shallow;
                if (commandName.equals("add") || commandName.equals("update") || commandName.equals("add_if_min")) {
                    StudyGroup newGroup = new SGParser(scriptScanner, new StandardPrinter()).parseStudyGroup();
                    newGroup.setLogin(getLogin());
                    shallow = new CommandShallow(commandName, commandArgument != null ? new String[]{commandArgument} : null, newGroup, login, password);
                } else {
                    shallow = new CommandShallow(commandName, commandArgument != null ? new String[]{commandArgument} : null, null, login, password);
                }
                Response response = sendShallow(shallow);

                if (!response.isSuccess()) {
                    logger.error("Script execution failed at command: " + commandName);
                    return ExitCode.ERROR;
                }
            }

            return ExitCode.OK;
        } catch (IOException e) {
            logger.error("Script execution failed", e);
            return ExitCode.ERROR;
        }
    }

    // Получение информации с сервера
    public Object[] getInfo() {
        CommandShallow shallow = new CommandShallow("info", null, null, login, password);
        try {
            Response response = sendShallow(shallow);
            return (Object[]) response.getData();
        } catch (IOException e) {
            logger.error("Fetching info failed", e);
            return null;
        }
    }

    // Получение текущего имени пользователя
    public String getCurrentUsername() {
        // Возвращаем текущий логин как имя пользователя
        return login;
    }

    // Добавление StudyGroup, если она минимальная
    public boolean addStudyGroupIfMin(StudyGroup studyGroup) {
        CommandShallow shallow = new CommandShallow("add_if_min", null, studyGroup, login, password);
        try {
            Response response = sendShallow(shallow);
            if (response.isSuccess()) {
                studyGroup.setId(Long.parseLong(response.getData().toString()));
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Adding study group if min failed", e);
            return false;
        }
    }

    // Перечисление для кодов выхода
    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
        ERROR_NULL_RESPONSE,
    }

    // Получение объекта UDPConnector
    public UDPConnector getConnector() {
        return connector;
    }
}
