package ru.itmo.server.core;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.commands.base.CommandContext;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.server.managers.DatabaseConnector;

import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

public class Handler implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(Handler.class);

    @Setter
    private static CollectionManager collectionManager;
    @Setter
    private static DatabaseConnector databaseConnector;
    @Setter
    private static CommandManager commandManager;

    private CommandShallow shallow;
    private SelectionKey key;
    private SocketAddress client;
    private ExecutorService senderPool;

    public Handler(CommandShallow shallow, SelectionKey key, SocketAddress client, ExecutorService senderPool) {
        this.shallow = shallow;
        this.key = key;
        this.client = client;
        this.senderPool = senderPool;
    }

    @Override
    public void run() {
        try {
            if (collectionManager == null) {
                throw new IllegalStateException("CollectionManager is not initialized");
            }

            CommandContext commandContext = new CommandContext(shallow.getArguments(),
                    shallow.getStudyGroup(),
                    collectionManager,
                    shallow.getLogin(),
                    shallow.getPassword());

            if (commandContext.getLogin() == null || commandContext.getPassword() == null) {
                Response response = new Response(false, "Для работы с системой требуется вход в систему");
                UDPSender sender = new UDPSender(response, key, client);
                senderPool.submit(sender);
                return;
            } else if (!(shallow.getCommand().equals("login") || shallow.getCommand().equals("register"))
                    && !(databaseConnector.isSignIn(commandContext.getLogin(), commandContext.getPassword()))) {
                Response response = new Response(false, "Неправильный логин или пароль");
                UDPSender sender = new UDPSender(response, key, client);
                senderPool.submit(sender);
                return;
            }

            Response response = commandManager.getCommand(shallow.getCommand()).execute(commandContext);
            UDPSender sender = new UDPSender(response, key, client);
            senderPool.submit(sender);
        } catch (Exception e) {
            logger.error("Error processing UDP data: " + e.getMessage(), e);
        }
    }
}
