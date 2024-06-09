package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

public class RemoveById extends Command {
    public RemoveById() {
        super("remove_by_id", "удалить элемент из коллекции по его id");
    }

    @Override
    public boolean validateArgs(String args, int length) {
        try {
            super.validateArgs(args, length);
            Long.parseLong(args);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Ошибка формата аргумента");
            return false;
        }
        return true;
    }

    @Override
    public Response execute(CommandContext context) {
        String args = context.getArgs().toString();
        CollectionManager<StudyGroup> collectionManager = context.getCollectionManager();

        try {
            collectionManager.removeById(Long.parseLong(args), context.getLogin());
            return new Response(true, "Удалена группа: ID " + args);
        } catch (Exception e) {
            return new Response(false, "Ошибка при удалении группы ID " + args + ": " + e.getMessage());
        }
    }
}
