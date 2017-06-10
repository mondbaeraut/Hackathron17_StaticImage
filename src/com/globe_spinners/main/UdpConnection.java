package com.globe_spinners.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class UdpConnection {

    private static UdpConnection instance;
    private static final int PORT = 1234;
    private static InetAddress address;
    private static final int SEND_PACKAGE_SIZE = 10;
    private DatagramSocket socket;

    private byte packageHead[];
    private byte sendBuffer[];

    private UdpConnection() {
        try {
            socket = new DatagramSocket();
           // address = InetAddress.getByName("172.22.32.226");
           address = InetAddress.getByName("192.168.0.103");
            packageHead = new byte[SEND_PACKAGE_SIZE];
            packageHead[0] = 1;
            packageHead[1] = '[';
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
        packageHead[0] = (byte) data.length;
        try {
            if (socket.isConnected()) {
                int currpos = 0;
                while (currpos < data.length) {
                    if (currpos == 0) {
                        socket.send(new DatagramPacket(packageHead, packageHead.length, address, PORT));
                        currpos++;
                    } else {
                        for(int i = 0; i < SEND_PACKAGE_SIZE; i++){
                            if(currpos < data.length) {
                                sendBuffer[i] = data[currpos];
                            }
                            currpos++;
                        }
                        socket.send(new DatagramPacket(sendBuffer, sendBuffer.length, address, PORT));
                        currpos += 1024;
                    }
                }
                packageHead[1] = ']';
                socket.send(new DatagramPacket(packageHead, packageHead.length, address, PORT));

            } else {
                System.out.println("not connected");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}