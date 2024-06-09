package ru.itmo.common.interfaces;

public interface Printer {

    void print(String text);

    void printf(String format, Object... args);
}
