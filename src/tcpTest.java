import java.io.BufferedReader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tcpTest {

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer();
        TCPClient tcpClient1 = new TCPClient(tcpServer.getIp(), tcpServer.getPort());
        TCPClient tcpClient2 = new TCPClient(tcpServer.getIp(), tcpServer.getPort());

        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(2);
            while (true) {
                Socket socket = tcpServer.initServer();
                BufferedReader bufferedReader = tcpServer.receiveMessages(socket);
                executorService.execute(() -> {
                    tcpServer.receiveMessages(socket);
                    while (true) {
                        String msg = tcpServer.readLine(bufferedReader);
                        System.out.println("Received: " + msg + " : " + Thread.currentThread().getName());
                    }


                });
            }
        }).start();

        new Thread(() -> {
            tcpClient1.sendMessage("HAllo");
            tcpClient1.sendMessage("ich");
            tcpClient1.sendMessage("bin");
            tcpClient1.sendMessage("neu");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tcpClient1.sendMessage("HAllo");
            tcpClient1.sendMessage("ich");
            tcpClient1.sendMessage("bins");
            tcpClient1.sendMessage("nochmal");

        }).start();
        new Thread(() -> {
            tcpClient2.sendMessage("2HAllo");
            tcpClient2.sendMessage("2ich");
            tcpClient2.sendMessage("2bin");
            tcpClient2.sendMessage("2neu");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tcpClient2.sendMessage("2HAllo");
            tcpClient2.sendMessage("2ich");
            tcpClient2.sendMessage("2bins");
            tcpClient2.sendMessage("2nochmal");

        }).start();

    }
}
