package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;

public class Exit extends Command {
    public Exit() {
        super("exit", "завершить программу (без сохранения в файл)");
    }

    @Override
    public Response execute(CommandContext context) {
        System.out.println("Клиент остановлен!");
        return new Response(true, "exit");
    }
}
