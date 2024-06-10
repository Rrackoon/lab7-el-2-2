package ru.itmo.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import ru.itmo.client.core.Console;
import ru.itmo.client.core.UDPConnector;
import ru.itmo.client.core.UDPReader;
import ru.itmo.client.core.UDPSender;
import ru.itmo.common.commands.*;
import ru.itmo.common.commands.base.Command;
import ru.itmo.common.exceptions.CommandIOException;
import ru.itmo.common.exceptions.InputArgumentException;
import ru.itmo.common.interfaces.Printer;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.utility.IOProvider;
import ru.itmo.common.utility.StandardPrinter;
import ru.itmo.client.controller.*;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import static javafx.application.Application.launch;

public class Main  {


    public static void main(String[] args) throws InputArgumentException {
        if (args.length != 0) {
            throw new InputArgumentException("Error! Got " + Integer.valueOf(args.length) + " arguments when 0 required");
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nВыключаем клиент");
        }));
        Scanner scanner = new Scanner(System.in);
        Printer printer = new StandardPrinter();
        IOProvider provider = new IOProvider(scanner, printer);
        CommandManager commandmanager = new CommandManager(provider);
        UDPConnector connector = new UDPConnector("localhost", 3940);
        Console console = new Console(commandmanager, provider);
        String[] comnames = {"help", "info", "show", "add", "update", "remove_by_id", "clear",
                "execute_script", "exit", "add_if_min", "count_less_than_group_admin", "update",
                "print_asceding", "remove_first", "login", "register"};
        Command[] coms = {new Help(), new Info(), new Show(), new Add(), new Update(),
                new RemoveById(), new Clear(), new ExecuteScript(), new Exit(),
                new AddIfMin(), new CountLesAdminName(), new Update(), new PrintAsceding(), new RemoveFirst(), new LogIn(), new Register()};
        for (int i = 0; i < coms.length; ++i) {
            try {
                commandmanager.createCommand(comnames[i], coms[i]);
            } catch (CommandIOException e) {
                System.out.println(e.getMessage());
            }
        }

        console.start(connector);
    }
}
