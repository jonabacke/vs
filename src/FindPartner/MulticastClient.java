package FindPartner;

import Config.ConfigFile;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class MulticastClient {
    private static final Logger logger = Logger.getGlobal();

    private MulticastSocket socket = null;
    InetSocketAddress group = null;
    NetworkInterface networkInterface;

    public MulticastClient() {
        this.initSocket();
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeSocket));
    }

    private void initSocket() {
        try {
            this.socket = new MulticastSocket();
            group = new InetSocketAddress(InetAddress.getByName("239.0.0.1"), 9000);
            this.networkInterface = NetworkInterface.getByName(ConfigFile.NETWORK_INTERFACE);
            this.socket.joinGroup(group, this.networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishMsg(String msg) {
        if (msg == null) throw new IllegalArgumentException();
        byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = null;
            packet = new DatagramPacket(buffer, buffer.length, group);
        logger.finest(()-> "send Msg: " + msg);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        try {
            this.socket.leaveGroup(group, this.networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket.close();
    }

}
