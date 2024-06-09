package ru.itmo.common.utility;


import ru.itmo.common.interfaces.Printer;

public class StandardPrinter implements Printer {
    @Override
    public void print(String text) {
        System.out.println(text);
    }

    @Override
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

}
