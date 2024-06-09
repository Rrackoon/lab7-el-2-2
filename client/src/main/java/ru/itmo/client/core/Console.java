package ru.itmo.client.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.exceptions.CommandIOException;
import ru.itmo.common.exceptions.RecursionException;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.models.StudyGroup;
import ru.itmo.common.parser.CommandParser;
import ru.itmo.common.utility.IOProvider;
import ru.itmo.common.utility.SGParser;
import ru.itmo.common.utility.StandardPrinter;

import java.io.*;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class Console {
    private static final Logger logger = LoggerFactory.getLogger(Console.class);
    private static boolean active;
    private static UDPSender sender;
    private static UDPReader reader;
    private final CommandManager commandManager;
    private final IOProvider provider;
    private final Scanner scanner;
    private final LinkedList<String> commandsStack;
    private boolean authorized;
    private String login;
    private String password;
    private Set<String> scriptSet = new HashSet<>();

    public Console(CommandManager commandManager, IOProvider provider) {
        this.scanner = new Scanner(System.in);
        Console.active = true;
        this.commandManager = commandManager;
        this.provider = provider;
        this.commandsStack = new LinkedList<>();
    }

    public static void stop() {
        active = false;
        logger.info("Console stopped.");
    }

    public static void executeCommand(CommandShallow shallow, IOProvider provider, CommandManager commandManager) throws IOException {
        if (shallow.getCommand().contains("add") || shallow.getCommand().equals("update")) {
            try {
                StudyGroup sg = new SGParser(provider.getScanner(), provider.getPrinter(), provider.isPrintValue()).parseStudyGroup();
                sg.setLogin(shallow.getLogin());
                shallow.setStudyGroup(sg);
            } catch (Exception e) {
                logger.error("Error creating StudyGroup: {}", e.getMessage());
                System.out.println(e.getMessage());
                return;
            }
        }

        try {
            handleResponse(sender.executeCommand(shallow));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleResponse(Response response) throws IOException, ClassNotFoundException {
        System.out.println(response);
    }


    public boolean isActive() {
        return active;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public void start(UDPConnector connector) {
        logger.info("Starting console and connecting to server...");
        int port = 9999; // укажите правильный порт
        if (connector.connect()) {
            reader = new UDPReader(connector.getDatagramSocket());
            sender = new UDPSender(connector.getDatagramSocket(), connector.getServerAddress(), port, reader);
            while (Console.active) {
                readCommand(provider);
            }
        } else {
            logger.error("Failed to connect to the server.");
        }
    }

    public void print(String line) {
        if (line != null) {
            System.out.print(line);
        }
    }

    public void println(String line) {
        if (line != null) {
            System.out.println(line);
        } else {
            System.out.println();
        }
    }

    public void readCommand(IOProvider provider) {
        logger.debug("Awaiting command input...");
        System.out.print("Введите команду (или help): ");
        String[] com;
        com = scanner.nextLine().split("\\s");
        if (com.length == 0 || com[0].isEmpty()) {
            logger.warn("Empty command entered.");
            System.out.println("Команда не должна быть пустой");
            return;
        }

        String commandName = com[0];
        String arg = String.join(" ", com.length > 1 ? com[1] : "");

        try {
            Command command = commandManager.getCommand(commandName);
            if (command == null) {
                logger.warn("No such command: {}", commandName);
                System.out.println("Нет такой команды");
                return;
            }
            if ("login".equals(command.getName()) || "register".equals(command.getName())) {
                System.out.print("Введите логин: ");
                login = scanner.nextLine().trim();
                System.out.print("Введите пароль: ");
                password = scanner.nextLine().trim();
            }
            CommandShallow shallow = new CommandShallow(command.getName(), arg, login, password);
            if (!argCheck(commandName, arg)) {
                return;
            }

            if (command.getName().contains("execute_script")) {
                try {
                    scriptMode(com[1]);
                } catch (Exception e) {
                    System.out.println("Something went wrong while running script.");
                }
                return;
            }

            executeCommand(shallow, provider, commandManager);

        } catch (CommandIOException e) {
            logger.error("CommandIOException occurred: {}", e.getMessage());
            System.out.println("Введена несуществующая команда");
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage());
            System.out.println(e.getMessage());
        }
    }

    public boolean scriptMode(String fileName) {
        scriptSet.add(fileName);
        if (!new File(fileName).exists()) {
            return false;
        }

        String[] userCommand;
        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();

            do {
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                if (userCommand[0].equals("execute_script")) {
                    if (scriptSet.contains(userCommand[1])) throw new RecursionException();
                }
                Command command = commandManager.getCommand(userCommand[0]);
                CommandShallow shallow = new CommandShallow(command.getName(), userCommand[1], login, password);
                executeCommand(shallow, provider, commandManager);
            } while (scriptScanner.hasNextLine());
        } catch (NoSuchElementException | IllegalStateException exception) {
            logger.error("Ошибка чтения из скрипта.");
            return false;
        } catch (FileNotFoundException exception) {
            logger.error("Файл не найден");
            return false;
        } catch (RecursionException exception) {
            logger.error("Обнаружена рекурсия");
            return false;
        } catch (CommandIOException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            scriptSet.remove(fileName);
        }
        return true;
    }

    private boolean argCheck(String name, String arg) {
        if ((name.equals("execute_script") || name.equals("count_less_than_group_admin")) && !arg.isEmpty()) {
            return true;
        } else if (name.equals("update") || name.equals("remove_by_id")) {
            try {
                Long.parseLong(arg);
            } catch (NumberFormatException e) {
                logger.warn("Argument value is not a long: {}", arg);
                System.out.println("Значение аргумента не long");
                return false;
            }
            return true;
        }
        return true;
    }
}
