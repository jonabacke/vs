package Middleware;

import FindPartner.MulticastServer;
import Lamport.TCPServer;

import java.io.BufferedReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class CallHandler {
    private MulticastServer multicastServer;
    private final Map<String, IMiddlewareCallableStub> stubs;
    AtomicBoolean running;

    public CallHandler() {
        this.stubs = new HashMap<>();
        running = new AtomicBoolean(false);
    }

    public void register(Service service, IMiddlewareCallableStub stub) {
        this.stubs.put(service.getServiceClassName(), stub);

        if (service.isReliable() && !this.running.get()) {
            TCPServer server = service.getTcpServer();
            this.runTCPServer(server);

        } else if (!service.isReliable()) {
            this.multicastServer = new MulticastServer();
            this.runMulticastServer();
        }

    }

    private void runTCPServer(TCPServer tcpServer) {
        running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running.set(false)));
        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            while (running.get()) {
                Socket socket = tcpServer.initServer();
                BufferedReader bufferedReader = tcpServer.receiveMessages(socket);
                executorService.execute(() -> {
                    while (running.get()) {
                        String msg = tcpServer.readLine(bufferedReader);
                        Logger.getGlobal().info(msg);
                        this.callBack(msg);
                    }
                });
            }
        }).start();
    }

    private void runMulticastServer() {
        AtomicBoolean running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running.set(false)));
        new Thread(() -> {
            while (running.get()) {
                String msg = this.multicastServer.receive();
                this.callBack(msg);
            }
        }).start();
    }

    public void callBack(String msg) {
        Wrapper wrapper = Marshaller.unpack(msg);
        Logger.getGlobal().fine(wrapper.getClassName());
        for (String s : this.stubs.keySet()) {
            Logger.getGlobal().fine(s);
        }
        this.stubs.get(wrapper.getClassName()).call(wrapper.getMethodName(), wrapper.getObjectParams(), wrapper.getClassParams());
    }
}
