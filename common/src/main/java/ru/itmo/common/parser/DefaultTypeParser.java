package ru.itmo.common.parser;

import ru.itmo.common.exceptions.InterruptCommandException;
import ru.itmo.common.interfaces.Parser;
import ru.itmo.common.interfaces.Printer;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class DefaultTypeParser implements Parser {
    private final Scanner input;
    private final Printer printer;

    public DefaultTypeParser(Scanner scanner, Printer printer) {
        this.input = scanner;
        this.printer = printer;
    }

    // Метод для ввода строки
    public String parseString(String name, String descr) throws NoSuchElementException {
        if (descr == null || descr.isEmpty()) {
            printer.printf("\nEnter %s:\n", name);
        } else {
            printer.printf("\nEnter %s (%s):\n", name, descr);
        }
        String line = input.nextLine().strip();

        if ("exit".equals(line)) {
            throw new InterruptCommandException();
        }
        return line.isEmpty() ? null : line;
    }

    // Универсальный метод для парсинга числовых типов
    private <T extends Number> T parseNumber(String name, String descr, NumberParser<T> parser, String typeName) {
        while (true) {
            try {
                String line = parseString(name, descr);
                return parser.parse(line);
            } catch (NumberFormatException exception) {
                printer.printf("Invalid %s.%n", typeName);
            }
        }
    }

    public Integer parseInt(String name, String descr) {
        return parseNumber(name, descr, Integer::parseInt, "integer");
    }

    public Long parseLong(String name, String descr) {
        return parseNumber(name, descr, Long::parseLong, "long");
    }

    public Float parseFloat(String name, String descr) {
        return parseNumber(name, descr, Float::parseFloat, "float");
    }

    public Double parseDouble(String name, String descr) {
        return parseNumber(name, descr, Double::parseDouble, "double");
    }

    public <T extends Enum<T>> T parseEnum(Class<T> enumType, String name, String descr) {
        while (true) {
            try {
                String line = parseString(name, descr);
                return (line != null) ? Enum.valueOf(enumType, line) : null;
            } catch (IllegalArgumentException exception) {
                printer.printf("Invalid %s.%n", name);
            }
        }
    }

    public void print(String text) {
        printer.print(text);
    }

    @FunctionalInterface
    private interface NumberParser<T extends Number> {
        T parse(String str) throws NumberFormatException;
    }
}
