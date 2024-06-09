package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

public class PrintAsceding extends Command {
    public PrintAsceding() {
        super("print_asceding", "вывести все элементы коллекции по возрастанию");
    }

    @Override
    public Response execute(CommandContext context) {
        CollectionManager<StudyGroup> collectionManager = context.getCollectionManager();

        String response = String.join("\n", collectionManager.getCollection().stream()
                .sorted()
                .map(StudyGroup::toString)
                .toArray(String[]::new));

        return new Response(true, response);
    }
}
