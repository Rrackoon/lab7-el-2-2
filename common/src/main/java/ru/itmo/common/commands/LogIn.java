package ru.itmo.common.commands;

import ru.itmo.common.commands.base.Command;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.interfaces.Accessible;

public class LogIn extends Command {
    private Accessible connector;

    public LogIn() {
        super("login", "войти в систему");
    }

    public LogIn(Accessible connector) {
        this();
        this.connector = connector;
    }

    @Override
    public Response execute(CommandContext context) {
        try {
            if (context.getLogin() == null || context.getLogin().isEmpty() ||
                    context.getPassword() == null || context.getPassword().isEmpty()) {
                return new Response(false, "Некорректный логин или пароль.");
            } else {
                String message = connector.signIn(context.getLogin(), context.getPassword());
                return new Response(true, message);
            }
        } catch (Exception e) {
            return new Response(false, "Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
