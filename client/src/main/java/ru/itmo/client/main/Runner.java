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
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);
    private static UDPSender sender;
    private static UDPReader reader;
    @Setter @Getter
    private String login;
    @Setter @Getter
    private String password;
    private IOProvider provider;
    private CommandManager commandManager;
    private UDPConnector connector;
    private Console console;

    public Runner() {
        this.provider = initializeIOProvider();
        this.commandManager = initializeCommandManager(provider);
        this.connector = initializeUDPConnector();
        this.connect();
    }

    public boolean connect(){
        logger.info("Starting console and connecting to server...");
        int port = 3940;
        if (connector.connect()) {
            reader = new UDPReader(connector.getDatagramSocket());
            sender = new UDPSender(connector.getDatagramSocket(), connector.getServerAddress(), port, reader);
            return true;
        } else {
            logger.error("Failed to connect to the server.");
            return false;
        }
    }

    public Runner(UDPConnector connector) {
        this.connector = connector;
        initializeCommandManager(provider);
    }

    private IOProvider initializeIOProvider() {
        Scanner scanner = new Scanner(System.in);
        Printer printer = new StandardPrinter();
        return new IOProvider(scanner, printer);
    }

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

    private UDPConnector initializeUDPConnector() {
        return new UDPConnector("localhost", 1234);
    }

    public Response sendShallow(CommandShallow shallow) throws IOException {
        return sender.executeCommand(shallow);
    }

    public Response executeLogin(String username, String password) {
        CommandShallow shallow = new CommandShallow("login", new String[]{username, password}, null, username, password);
        try {
            return sendShallow(shallow);
        } catch (IOException e) {
            logger.error("Login failed", e);
            return null;
        }
    }

    public boolean executeRegister(String username, String password) {
        CommandShallow shallow = new CommandShallow("register", new String[]{username, password}, null, username, password);
        try {
            Response response = sendShallow(shallow);
            return response.isSuccess();
        } catch (IOException e) {
            logger.error("Registration failed", e);
            return false;
        }
    }

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

    public boolean addStudyGroup(StudyGroup studyGroup) {
        CommandShallow shallow = new CommandShallow("add", null, studyGroup, login, password);
        try {
            Response response = sendShallow(shallow);
            if (response.isSuccess()) {
                studyGroup.setId(Long.parseLong(response.getData().toString()));
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Adding study group failed", e);
            return false;
        }
    }

    public void updateStudyGroup(StudyGroup studyGroup) {
        CommandShallow shallow = new CommandShallow("update", studyGroup.getId(), studyGroup, login, password);
        try {
            sendShallow(shallow);
        } catch (IOException e) {
            logger.error("Updating study group failed", e);
        }
    }

    public void deleteStudyGroup(StudyGroup studyGroup) {
        CommandShallow shallow = new CommandShallow("remove_by_id", new String[]{String.valueOf(studyGroup.getId())}, null, login, password);
        try {
            sendShallow(shallow);
        } catch (IOException e) {
            logger.error("Deleting study group failed", e);
        }
    }

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

    public long getCurrentUserId() {
        // Implement this method to return the current user ID
        // Assuming there's a way to retrieve this from the `runner`
        return -1; // Placeholder
    }

    public String getCurrentUsername() {
        // Implement this method to return the current username
        // Assuming there's a way to retrieve this from the `runner`
        return login; // Placeholder
    }

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

    public Response sumOfPrice() {
        CommandShallow shallow = new CommandShallow("sum_of_price", null, null, login, password);
        try {
            return sendShallow(shallow);
        } catch (IOException e) {
            logger.error("Sum of price command failed", e);
            return null;
        }
    }

    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
        ERROR_NULL_RESPONSE,
    }

    public void start() {
        console.start(connector);
    }

    public UDPConnector getConnector() {
        return connector;
    }
}
