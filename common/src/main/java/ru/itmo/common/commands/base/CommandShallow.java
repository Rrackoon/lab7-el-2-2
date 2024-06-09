package ru.itmo.common.commands.base;

import javafx.beans.binding.ObjectBinding;
import ru.itmo.common.models.StudyGroup;

import java.io.Serial;
import java.io.Serializable;

public class CommandShallow implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String command;
    private Object args;
    private StudyGroup studyGroup;
    private String login;
    private String password;

    public CommandShallow() {
        this.command = null;
        this.args = null;
        this.studyGroup = null;
        this.login = null;
        this.password = null;
    }

    public CommandShallow(String command, String args) {
        this.command = command;
        this.args = args;
        this.studyGroup = null;
        this.login = null;
        this.password = null;
    }

    public CommandShallow(String command, Object args, String login, String password) {
        this.command = command;
        this.args = args;
        this.studyGroup = null;
        this.login = login;
        this.password = password;
    }

    public CommandShallow(String command, Object args, StudyGroup studyGroup, String login, String password) {
        this.command = command;
        this.args = args;
        this.studyGroup = studyGroup;
        this.login = login;
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public Object getArguments() {
        return args;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }
}
