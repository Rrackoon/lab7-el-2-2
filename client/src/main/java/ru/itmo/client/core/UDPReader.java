package ru.itmo.client.core;

import ru.itmo.common.commands.base.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReader {

    private final DatagramSocket datagramSocket;

    public UDPReader(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public Response readResponse() throws IOException, ClassNotFoundException {
        System.out.println("in readResponse");
        Response response = null;
        byte[] buffer = new byte[100000];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.setSoTimeout(3000);
        datagramSocket.receive(packet);
        byte[] data = packet.getData();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        System.out.println("Читаем ответ");
        try {
            response = (Response) ois.readObject();
            System.out.println("Ответ получен: " + response);
        } catch (Exception e) {
            System.out.println("Ответ не является экземпляром класса Response");
        }
        return response;
    }
}
