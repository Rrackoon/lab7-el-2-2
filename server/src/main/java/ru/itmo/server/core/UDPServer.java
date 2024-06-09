package ru.itmo.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.exceptions.InputArgumentException;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.managers.CommandManager;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer extends Thread {
    private final CommandManager commandManager;
    private final CollectionManager collection;
    private final Logger logger = LoggerFactory.getLogger(UDPServer.class);
    private UDPReader reader;
    private UDPConnector connector;
    private ExecutorService handlerPool;
    private ExecutorService senderPool;

    public UDPServer(CommandManager commandManager, CollectionManager collection) throws InputArgumentException, IOException {
        this.commandManager = commandManager;
        this.collection = collection;
        connector = new UDPConnector();
        UDPReader.setCollection(collection);
        UDPReader.setCommandManager(commandManager);
        Handler.setCollectionManager(collection);
        Handler.setCommandManager(commandManager);
        this.handlerPool = Executors.newCachedThreadPool();
        this.senderPool = Executors.newFixedThreadPool(10);
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    // Ожидание событий на селекторе
                    if (connector.getSelector().select() > 0) {
                        logger.debug("In read");
                        Set<SelectionKey> keys = connector.getSelector().selectedKeys(); // Получение ключей событий
                        Iterator<SelectionKey> iterator = keys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            if (key.isReadable()) {
                                try {
                                    key.interestOps(key.interestOps() & ~SelectionKey.OP_READ); // Установка операций интереса
                                    new UDPReader(key, handlerPool, senderPool).start(); // Запуск нового потока для чтения данных
                                } catch (Exception e) {
                                    logger.error("Can't read request: {}", e.getMessage(), e);
                                }
                            }
                            iterator.remove();
                        }
                        logger.debug("End of cycle");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка в цикле: " + e.getMessage());
        }
    }
}