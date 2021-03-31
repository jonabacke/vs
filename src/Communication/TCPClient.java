package Communication;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPClient {
    private PrintWriter printWriter;

    public TCPClient(String ip, int port) {
        init(ip, port);
    }

    private void init(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        Logger.getGlobal().info("Send: " + msg);
        this.printWriter.println(msg);
    }
}
