package ru.itmo.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.commands.base.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class UDPSender implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UDPSender.class);
    private final DatagramChannel datagramChannel;
    private Response response;
    private SocketAddress client;

    public UDPSender(Response response, SelectionKey key, SocketAddress client) throws IOException {
        this.response = response;
        this.datagramChannel = (DatagramChannel) key.channel();
        this.datagramChannel.configureBlocking(false);
        this.client = client;
    }

    public void run() {
        try {
            System.out.println("Передача ответа");
            logger.debug("response: {} chunk :", response);

            ByteArrayOutputStream baos = new ByteArrayOutputStream(); // для записи объекта в массив байтов
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(response);
            byte[] arr = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(arr); // обертка массива байтов

            datagramChannel.send(buffer, client);
        } catch (IOException e) {
            logger.error("Error sending UDP data: " + e.getMessage(), e);
        }
    }
}
