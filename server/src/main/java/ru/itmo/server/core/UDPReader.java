package ru.itmo.server.core;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.managers.CollectionManager;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.models.StudyGroup;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;

public class UDPReader extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(UDPReader.class);

    @Setter
    public static CollectionManager<StudyGroup> collection;

    @Setter
    public static CommandManager commandManager;

    private ByteBuffer in_buffer;
    private DatagramChannel channel;
    private SocketAddress client;
    private CommandShallow shallow;
    private SelectionKey key;
    private ExecutorService handlerPool;
    private ExecutorService senderPool;

    public UDPReader(SelectionKey key, ExecutorService handlerPool, ExecutorService senderPool) throws Exception {
        this.key = key;
        in_buffer = ByteBuffer.allocateDirect(65507);
        this.channel = (DatagramChannel) key.channel();
        this.handlerPool = handlerPool;
        this.senderPool = senderPool;
    }

    public CommandShallow getShallow() {
        return shallow;
    }

    public SocketAddress getClient() {
        return this.client;
    }

    @Override
    public void run() {
        try {
            client = receive();
        } catch (Exception e) {
            logger.error("Error during receive: {}", e.getMessage(), e);
        } finally {
            key.interestOps(SelectionKey.OP_READ);
            key.selector().wakeup();
        }
    }

    public SocketAddress receive() {
        try {
            in_buffer.clear();
            client = channel.receive(in_buffer);
            in_buffer.flip();

            byte[] data = new byte[in_buffer.limit()];
            in_buffer.get(data);

            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInput in = new ObjectInputStream(bis);
            shallow = (CommandShallow) in.readObject();

            logger.info("Command received: " + shallow.getCommand());

            Handler handler = new Handler(shallow, key, client, senderPool);
            handlerPool.submit(handler);
        } catch (Exception e) {
            logger.error("Error receiving UDP data: " + e.getMessage(), e);
        }
        return client;
    }
}
