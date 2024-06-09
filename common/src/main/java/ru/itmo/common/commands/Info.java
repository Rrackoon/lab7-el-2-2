package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

public class Info extends Command {
    public Info() {
        super("info", "вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
    }

    @Override
    public Response execute(CommandContext context) {
        CollectionManager<StudyGroup> collectionManager = context.getCollectionManager();

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Коллекция: ").append(collectionManager.getCollection().getClass().getName()).append("\n")
                .append("Количество групп: ").append(collectionManager.getCollection().size()).append("\n")
                .append("Дата создания: ").append(collectionManager.getCreatedAt());

        return new Response(true, responseBuilder.toString());
    }
}
