package ru.itmo.client.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPConnector {

    private static final Logger logger = LoggerFactory.getLogger(UDPConnector.class);
    private final int serverPort;
    private final String host;
    private DatagramSocket datagramSocket;
    private SocketAddress serverAddress;

    public UDPConnector(String host, int port) {
        this.host = host;
        this.serverPort = port;
        this.datagramSocket = null;
        this.serverAddress = null;
    }

    public boolean connect() {
        try {
            this.serverAddress = new InetSocketAddress(host, serverPort);
            this.datagramSocket = new DatagramSocket();
            this.datagramSocket.connect(serverAddress);
            logger.info("Connected to {} on port {}", host, serverPort);
            return true;
        } catch (IOException e) {
            logger.error("Could not connect to {} on port {}", host, serverPort, e);
            return false;
        }
    }

    public int getServerPort() {
        return serverPort;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public SocketAddress getServerAddress() {
        return serverAddress;
    }
}
