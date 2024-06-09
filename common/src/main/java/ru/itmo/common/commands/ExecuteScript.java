package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.parser.CommandParser;
import ru.itmo.common.utility.IOProvider;
import ru.itmo.common.utility.StandardPrinter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class ExecuteScript extends Command {
    private final int recDepth;

    public ExecuteScript() {
        super("execute_script", "считать и исполнить скрипт из указанного файла");
        this.recDepth = 1;
    }

    public ExecuteScript(int recDepth) {
        super("execute_script", "считать и исполнить скрипт из указанного файла");
        this.recDepth = recDepth;
    }

    @Override
    public Response execute(CommandContext context) {
        String fileName = context.getArgs().toString();
        try (FileReader fileReader = new FileReader(fileName)) {
            var provider = new IOProvider(new Scanner(fileReader), new StandardPrinter(), true);
            var commandManager = new CommandManager(provider);
            var commandParser = new CommandParser(commandManager, provider, recDepth + 1);
            commandParser.run();
            return new Response(true, "Скрипт выполнен успешно");
        } catch (FileNotFoundException e) {
            return new Response(false, "Файл не найден или доступ запрещен (чтение)");
        } catch (IOException e) {
            return new Response(false, "Что-то пошло не так при чтении файла");
        }
    }

    @Override
    public void execute(String args) {
        if (!validateArgs(args, 2)) {
            return;
        }
        String fileName = args;
        try (FileReader fileReader = new FileReader(fileName)) {
            var provider = new IOProvider(new Scanner(fileReader), new StandardPrinter(), true);
            var commandManager = new CommandManager(provider);
            var commandParser = new CommandParser(commandManager, provider, recDepth + 1);
            commandParser.run();
            System.out.println("Скрипт выполнен успешно");
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден или доступ запрещен (чтение)");
            System.out.println("Нет такого файла");
        } catch (IOException e) {
            System.out.println("Что-то пошло не так при чтении файла");
        }
    }
}
