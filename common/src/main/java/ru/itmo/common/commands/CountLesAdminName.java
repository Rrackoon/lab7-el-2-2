package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

public class CountLesAdminName extends Command {
    public CountLesAdminName() {
        super("count_less_than_group_admin",
                "вывести количество элементов, значение поля groupAdmin которых меньше заданного");
    }

    @Override
    public Response execute(CommandContext context) {
        String name = context.getArgs().toString(); // Получение имени из аргументов
        CollectionManager<StudyGroup> collectionManager = context.getCollectionManager();

        long count = collectionManager.getCollection().stream()
                .filter(d -> d.getGroupAdmin() != null && name.compareTo(d.getGroupAdmin().getName()) > 0)
                .count();

        String message = "Количество элементов, значение поля groupAdmin которых меньше заданного (" + name + "): " + count;
        return new Response(true, message);
    }
}
