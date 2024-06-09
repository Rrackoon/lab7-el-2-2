package ru.itmo.common.parser;

import ru.itmo.common.exceptions.ExitException;
import ru.itmo.common.exceptions.InterruptCommandException;
import ru.itmo.common.exceptions.InvalidArgsException;
import ru.itmo.common.exceptions.RecursionException;
import ru.itmo.common.interfaces.Printer;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.utility.IOProvider;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class CommandParser extends DefaultTypeParser {
    private final static int MAX_REC_DEPTH = 5;
    private final CommandManager commandManager;
    private final IOProvider provider;
    private final int recDepth;

    public CommandParser(CommandManager commandManager, IOProvider provider, int recDepth) {
        super(provider.getScanner(), provider.getPrinter());
        this.commandManager = commandManager;
        this.provider = provider;
        this.recDepth = recDepth;
    }

    // Выполнение обработки команд
    public void run() {
        Scanner scanner = provider.getScanner();
        Printer printer = provider.getPrinter();

        // Проверка, чтобы рекурсия не превышала максимальное значение
        if (recDepth > MAX_REC_DEPTH) {
            throw new RecursionException();
        }

        // Обработка команд пользователя
        while (true) {
            try {
                printer.printf("Enter command:\n");
                String line = scanner.nextLine();
                String[] splitLine = line.strip().split("\\s+"); // Разделение на массив строк
                String commandName = splitLine[0].toLowerCase(); // Приведение к нижнему регистру
                String args = splitLine.length > 1 ? splitLine[1] : ""; // Получение аргументов команды

                try {
                    commandManager.execute(commandName, args);
                } catch (Exception e) {
                    printer.printf("Ошибка выполнения команды скрипта: %s\n", e.getMessage());
                }

                if (!commandManager.execute(commandName, args)) {
                    printer.print("Invalid command");
                }
            } catch (InterruptCommandException e) {
                printer.print("\nExited\n");
            } catch (InvalidArgsException e) {
                printer.print("Invalid arguments. Use command \"help\" to find correct ones.");
            } catch (NoSuchElementException e) {
                printer.print("EOF");
                break;
            } catch (RecursionException e) {
                printer.print("Recursion depth exceeded!");
                break;
            } catch (ExitException e) {
                provider.closeScanner();
                printer.print("Program has finished. Good luck!");
                break;
            } catch (Exception e) {
                printer.printf("Error occurred: %s\n", e);
            }
        }
    }
}
