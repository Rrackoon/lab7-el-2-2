package ru.itmo.client.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.common.commands.base.CommandShallow;
import ru.itmo.common.commands.base.Response;
import ru.itmo.common.managers.CommandManager;
import ru.itmo.common.models.StudyGroup;
import ru.itmo.common.utility.IOProvider;
import ru.itmo.common.utility.SGParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class UDPSender {
    private static final Logger logger = LoggerFactory.getLogger(UDPSender.class);
    private static final int DATA_SIZE = 1024; // размер каждого чанка

    private final DatagramSocket datagramSocket;
    private final SocketAddress hostAddress;
    private final UDPReader reader;
    private int port;

    public UDPSender(DatagramSocket datagramSocket, SocketAddress hostAddress, int port, UDPReader reader) {
        this.datagramSocket = datagramSocket;
        this.hostAddress = hostAddress;
        this.reader=reader;
        this.port = port;
    }

    public Response executeCommand(CommandShallow shallow) throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(shallow);
            oos.flush();

            byte[] arr = baos.toByteArray();
            logger.debug("Sending command {}: {} bytes", shallow.getCommand(), arr.length);
            send(arr);

            return reader.readResponse();

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error executing command: {}", e.getMessage());
            System.out.println("Ошибка при выполнении команды: " + e.getMessage());
            return new Response(false, "ошибка чтения запроса");
        }
    }

    public void send(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, hostAddress);
        datagramSocket.send(packet);
    }
}
