package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;

public class Clear extends Command {
    public Clear() {
        super("clear", "очистить коллекцию");
    }

    @Override
    public Response execute(CommandContext context) {
        CollectionManager collectionManager = context.getCollectionManager();

        try {
            collectionManager.clear(context.getLogin());
            return new Response(true, "Коллекция очищена");
        } catch (Exception e) {
            return new Response(false, "Ошибка при очистке коллекции: " + e.getMessage());
        }
    }
}
