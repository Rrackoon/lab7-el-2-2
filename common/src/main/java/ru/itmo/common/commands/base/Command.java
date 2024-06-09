package ru.itmo.common.commands.base;

import ru.itmo.common.exceptions.InvalidArgsException;

import java.io.Serializable;

public abstract class Command implements Serializable {
    private String name;
    private String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Change the return type from Response[] to Response
    public abstract Response execute(CommandContext context);

    public void execute(String args) throws InvalidArgsException {
    }

    public boolean validateArgs(String args, int length) {
        if (length > 1 && args.compareTo("") == 0) {
            System.out.println("Отсутствие аргумента команды");
            return false;
        }
        return true;
    }
}
