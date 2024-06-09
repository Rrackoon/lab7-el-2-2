package ru.itmo.common.managers;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.exceptions.CommandIOException;
import ru.itmo.common.exceptions.InvalidArgsException;
import ru.itmo.common.utility.IOProvider;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static final Map<String, Command> commands = new HashMap<>();
    private IOProvider provider;

    public CommandManager(IOProvider provider) {
        this.provider = provider;
    }


    public CommandManager() {
    }

    public static String getCommandsDescription() {
        StringBuilder description = new StringBuilder();
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            description.append(entry.getKey()).append(" : ").append(entry.getValue().getDescription()).append("\n");
        }
        return description.toString();
    }

    public String getCommands() {
        String cmd = "";
        for (Map.Entry<String, Command> val : commands.entrySet()) {
            cmd += val.getValue().getName() + " : " + val.getValue().getDescription() + "\n";
        }
        return cmd;
    }

    public void createCommand(String name, Command command) throws CommandIOException {
        if (name.equals(null) || name.equals("^\\s*$")) {
            throw new CommandIOException("Error! Can't create command with name \"" + name + "\"");
        }
        commands.put(name, command);
    }

    public boolean execute(String commandName, String args) throws InvalidArgsException {
        if (commands.containsKey(commandName)) {
            commands.get(commandName).execute(args);
            return true;
        }
        return false;
    }

    public Command getCommand(String name) throws CommandIOException {
        if (commands.containsKey(name)) {
            return commands.get(name);
        }
        throw new CommandIOException("Error! Unknown command \"" + name + "\"");
    }
}

