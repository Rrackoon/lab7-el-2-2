package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.exceptions.InvalidArgsException;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

public class Update extends Command {
    public Update() {
        super("update", "обновить значение элемента коллекции, id которого равен заданному");
    }

    @Override
    public Response execute(CommandContext context) {
        String response;
        long id = Long.parseLong(context.getArgs().toString());
        StudyGroup studyGroup = context.getStudyGroup();
        CollectionManager<StudyGroup> collection = context.getCollectionManager();
        try {
            collection.update(id, context.getLogin(), studyGroup);
            response = "Обновлена группа: ID " + context.getArgs();
        } catch (Exception e) {
            response = "Ошибка при обновлении группы ID " + context.getArgs() + ": " + e.getMessage();
        }
        return new Response(true, response);
    }

    @Override
    public void execute(String args) throws InvalidArgsException {
        if (!validateArgs(args, 1)) throw new InvalidArgsException();
    }
}
