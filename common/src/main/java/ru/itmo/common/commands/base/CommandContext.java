package ru.itmo.common.commands.base;

import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.models.StudyGroup;

public class CommandContext {
    private Object args;
    private StudyGroup studyGroup;
    private CollectionManager collectionManager;
    private String login;
    private String password;

    public CommandContext(Object args, StudyGroup studyGroup,
                          CollectionManager collectionManager,
                          String login, String password) {
        this.args = args;
        this.studyGroup = studyGroup;
        this.collectionManager = collectionManager;
        this.login = login;
        this.password = password;
    }

    public CommandContext(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Object getArgs() {
        return args;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
