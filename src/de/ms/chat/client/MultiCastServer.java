package de.ms.chat.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;

public class MultiCastServer extends Thread
{

    private final int port = 1900;
    private InetAddress multicastAddress;
    private Window window;

    MultiCastServer(Window window)
    {
        try {
            multicastAddress = InetAddress.getByName("239.255.255.249");
            this.window = window;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try {
            MulticastSocket socket = new MulticastSocket(port);
            socket.setReuseAddress(true);
            socket.joinGroup(multicastAddress);
            System.out.println("Multicast Receiver running at:" + socket.getLocalSocketAddress());

            send(window.getClientName() + " ist beigetreten!");

            while (true) {
                byte[] rxbuf = new byte[8192];
                DatagramPacket packet = new DatagramPacket(rxbuf, rxbuf.length);

                socket.receive(packet);

                InetAddress addr = packet.getAddress();
                System.out.println("Response from: " + addr);
                ByteArrayInputStream in = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                BufferedInputStream bin = new BufferedInputStream(in);
                int byteRead;
                StringBuilder textBuilder = new StringBuilder();
                while ((byteRead = bin.read()) != -1) {
                    textBuilder.append((char) byteRead);
                }
                window.addMessage(textBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to network
     * @param message Message to be sent to network
     */
    void send(String message)
    {
        try {
            DatagramSocket udpSocket = new DatagramSocket();

            byte[] msg = message.getBytes();
            DatagramPacket packet = new DatagramPacket(msg, msg.length);
            packet.setAddress(multicastAddress);
            packet.setPort(port);
            udpSocket.send(packet);
            System.out.println("Sent a  multicast message.");

            udpSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
