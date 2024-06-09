package ru.itmo.common.utility;


import ru.itmo.common.interfaces.Printer;

import java.util.Scanner;

public class IOProvider {
    Printer printer;
    Scanner scanner;
    boolean printValue = false;

    public IOProvider(Scanner scanner, Printer printer) {
        this.scanner = scanner;
        this.printer = printer;
    }

    public IOProvider(Scanner scanner, Printer printer, boolean printValue) {
        this.scanner = scanner;
        this.printer = printer;
        this.printValue = printValue;
    }


    public Scanner getScanner() {
        return scanner;
    }

    public void closeScanner() {
        scanner.close();
    }

    public Printer getPrinter() {
        return printer;
    }

    public boolean isPrintValue() {
        return printValue;
    }
}
