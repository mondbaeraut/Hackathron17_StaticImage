package com.globe_spinners.main;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UdpConnection {

    private static UdpConnection instance;
    private static final int PORT = 1234;
    private static InetAddress address;
    private static final int SEND_PACKAGE_SIZE = 512;
    private DatagramSocket socket;

    private byte sendBuffer[];

    private UdpConnection() {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("192.168.0.103");
            address = InetAddress.getByName("127.0.0.1");
            sendBuffer = new byte[SEND_PACKAGE_SIZE];
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static UdpConnection getInstance() {
        if (instance == null) {
            instance = new UdpConnection();
        }
        return instance;
    }

    public void writeData(byte[] data) {
        socket.connect(address, PORT);

        try {
            if (socket.isConnected()) {
                int currpos = 0;
                while (currpos < data.length) {
                    if (currpos == 0) {
                        ByteBuffer b = ByteBuffer.allocate(4);
                        b.order(ByteOrder.LITTLE_ENDIAN);
                        b.putInt(data.length);
                        byte[] length = b.array();

                        for (int i = 0; i < 4; ++i) {
                            sendBuffer[i] = length[i];
                        }
                        sendBuffer[4] = '[';

                        int i;
                        for (i = 5; i < SEND_PACKAGE_SIZE; i++) {
                            if (currpos < data.length) {
                                sendBuffer[i] = data[i - 5];
                                currpos++;
                            } else {
                                sendBuffer[i] = ']';
                                ++i;
                                break;
                            }
                        }
                        socket.send(new DatagramPacket(sendBuffer, i, address, PORT));
                    } else {
                        int i;
                        for (i = 0; i < SEND_PACKAGE_SIZE; i++) {
                            if (currpos < data.length) {
                                sendBuffer[i] = data[currpos];
                                currpos++;
                            } else {
                                sendBuffer[i] = ']';
                                ++i;
                                break;
                            }
                        }
                        socket.send(new DatagramPacket(sendBuffer, i, address, PORT));
                    }
                }
            } else {
                System.out.println("not connected");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}