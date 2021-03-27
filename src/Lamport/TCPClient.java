package Lamport;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPClient {
    private Socket socket;
    private PrintWriter printWriter;

    public TCPClient(String ip, int port) {
        init(ip, port);
    }

    private void init(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.printWriter = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        Logger.getGlobal().info("Send: " + msg);
        this.printWriter.println(msg);
    }

    public void closeConnection() {
        this.printWriter.close();
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
