package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CommandManager;

public class Help extends Command {
    public Help() {
        super("help", "вывести справку по командам");
    }

    @Override
    public Response execute(CommandContext context) {
        String description = CommandManager.getCommandsDescription();
        return new Response(true, description);
    }
}
