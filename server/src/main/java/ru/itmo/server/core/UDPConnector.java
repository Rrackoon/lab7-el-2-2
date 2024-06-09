package ru.itmo.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class UDPConnector {
    private static final Logger logger = LoggerFactory.getLogger(UDPConnector.class);
    DatagramChannel channel;
    DatagramSocket socket;
    Selector selector;

    //SocketAddress client;
    public UDPConnector() {

        try {
            channel = DatagramChannel.open();
            selector = Selector.open();
            socket = channel.socket();
            SocketAddress address = new InetSocketAddress(1234);
            socket.bind(address);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            logger.info("UDP Connector initialized");
        } catch (Exception e) {
            logger.error("Error initializing UDP Connector: {}", e.getMessage(), e);
        }
    }

    public DatagramChannel getChannel() {
        return this.channel;
    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    public Selector getSelector() {
        return this.selector;
    }
}
