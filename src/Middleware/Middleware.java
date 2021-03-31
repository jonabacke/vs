package Middleware;

import Communication.MulticastClient;
import Communication.TCPClient;
import Communication.TCPServer;
import Config.NetworkTuple;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Middleware implements IMiddlewareInvoke, IMiddlewareRegisterService {
    private static final Logger logger = Logger.getGlobal();

    private final TCPServer tcpServer;
    private final CallHandler callHandler;
    private final Map<UUID, TCPClient> tcpClients;
    private final MulticastClient client;


    public Middleware() {
        this.tcpServer = new TCPServer();
        this.client = new MulticastClient();
        this.tcpClients = new HashMap<>();
        this.callHandler = new CallHandler();
    }

    public void setPartner(Map<UUID, NetworkTuple> partner) {
        for (Map.Entry<UUID, NetworkTuple> tuple : partner.entrySet()) {
            TCPClient tcpClient = new TCPClient(tuple.getValue().getIp(), tuple.getValue().getPort());
            tcpClients.put(tuple.getKey(), tcpClient);
        }
    }

    public TCPServer getTcpServer() {
        return tcpServer;
    }

    @Override
    public void register(String serviceName, IMiddlewareCallableStub stub, boolean isReliable) {
        this.callHandler.register(new Service(serviceName, isReliable, this.tcpServer), stub);
    }

    @Override
    public void invoke(UUID partnerUUID, Class<?> className, String function, Object[] values, boolean isReliable) {
        String msg = Marshaller.pack(className, function, values);
        Logger.getGlobal().info(msg);
        if (isReliable) {
            this.tcpClients.get(partnerUUID).sendMessage(msg);
        } else {
            this.client.publishMsg(msg);
        }

    }


}

