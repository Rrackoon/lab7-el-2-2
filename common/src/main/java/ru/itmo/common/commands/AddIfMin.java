package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

import java.util.Set;
import java.util.stream.Collectors;

public class AddIfMin extends Command {
    public AddIfMin() {
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
    }

    @Override
    public Response execute(CommandContext context) {
        StudyGroup studyGroup = context.getStudyGroup();
        CollectionManager<StudyGroup> collectionManager = context.getCollectionManager();

        // Собираем set из всех уникальных passportID
        Set<String> pids = collectionManager.getCollection().stream()
                .map(dr -> dr.getGroupAdmin().getPassportID())
                .collect(Collectors.toSet());

        if (pids.contains(studyGroup.getGroupAdmin().getPassportID())) {
            return new Response(false, "Нарушена уникальность passportID: " + studyGroup.getGroupAdmin().getPassportID());
        } else {
            StudyGroup minStudyGroup = collectionManager.min();
            if (minStudyGroup != null && studyGroup.getStudentsCount() < minStudyGroup.getStudentsCount()) {
                try {
                    collectionManager.push(studyGroup);
                    return new Response(true, "Элемент добавлен: " + studyGroup.getName());
                } catch (Exception e) {
                    return new Response(false, "Ошибка при добавлении элемента: " + e.getMessage());
                }
            } else {
                return new Response(false, "Группа не добавлена. Минимальная группа имеет количество студентов: " + (minStudyGroup != null ? minStudyGroup.getStudentsCount() : "N/A"));
            }
        }
    }

}
