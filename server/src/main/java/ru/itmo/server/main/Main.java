package ru.itmo.server.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.commands.*;
import ru.itmo.common.commands.base.Command;
import ru.itmo.common.exceptions.CommandIOException;
import ru.itmo.common.exceptions.InputArgumentException;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.server.core.Handler;
import ru.itmo.server.core.UDPServer;
import ru.itmo.server.managers.DatabaseConnector;
import ru.itmo.server.managers.StudyGroupCollectionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Scanner;


public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InputArgumentException, IOException {
        logger.info("Запуск сервера");
        DatabaseConnector databaseConnector;
        CollectionManager collection;
        try {
            databaseConnector = new DatabaseConnector();
            collection = new StudyGroupCollectionManager(databaseConnector);
            Handler.setDatabaseConnector(databaseConnector);
        } catch (SQLTimeoutException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (args.length != 0) {
            throw new InputArgumentException("Error! Got " + Integer.valueOf(args.length) + " arguments when 0 required");
        }

        CommandManager commandManager = new CommandManager();
        String[] comnames = {"help", "info", "show", "add", "update", "remove_by_id", "clear",
                "execute_script", "exit", "add_if_min", "count_less_than_group_admin", "update",
                "print_asceding", "remove_first", "login", "register"};
        Command[] coms = {new Help(), new Info(), new Show(), new Add(), new Update(),
                new RemoveById(), new Clear(), new ExecuteScript(), new Exit(),
                new AddIfMin(), new CountLesAdminName(), new Update(), new PrintAsceding(), new RemoveFirst(), new LogIn(databaseConnector), new Register(databaseConnector)};
        for (int i = 0; i < coms.length; ++i) {
            try {
                commandManager.createCommand(comnames[i], coms[i]);
            } catch (CommandIOException e) {
                logger.error(e.getMessage());
            }
        }

        UDPServer udpServer = new UDPServer( commandManager, collection);
        udpServer.start();

        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String serv_com = sc.nextLine();
            logger.info("Server command: {}", serv_com);
            if ("exit".equals(serv_com)) {
                System.exit(0);
            }
        }
    }
}