package ru.itmo.common.interfaces;

public interface Accessible {

    String signIn(String login, String password);

    String register(String login, String password);

    boolean checkOwnership(long id, String login);
}
