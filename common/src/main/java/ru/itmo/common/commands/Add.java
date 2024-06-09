package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

import java.util.Arrays;

public class Add extends Command {

    public Add() {
        super("add", "добавить новый элемент в коллекцию");
    }

    @Override
    public Response execute(CommandContext context) {
        StudyGroup studyGroup = context.getStudyGroup();
        CollectionManager<StudyGroup> collectionManager = context.getCollectionManager();

        String[] pids = collectionManager.getCollection().stream()
                .map(dr -> dr.getGroupAdmin().getPassportID())
                .toArray(String[]::new);

        if (Arrays.asList(pids).contains(studyGroup.getGroupAdmin().getPassportID())) {
            return new Response(false, "Нарушена уникальность passportID: " + studyGroup.getGroupAdmin().getPassportID());
        } else if (!studyGroup.validate()) {
            return new Response(false, "Группа не валидна");
        } else {
            try {
                int id= collectionManager.push(studyGroup);
                return new Response(true, "Добавлена группа: " + studyGroup.getName(),id);
            } catch (Exception e) {
                return new Response(false, "Ошибка при добавлении группы: " + e.getMessage());
            }
        }
    }

}
