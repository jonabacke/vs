import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UDPCom {

    Map<UUID,?> robos; 
    
    public UDPCom() {
        this.robos = new HashMap<>();
    }

    public void sendTo(List<UUID> ids, Request request) {
        //
    }

    public Request recvFrom(List<UUID> ids) {
        // UDPServer server
        return null;
    }

    private void udpServer() {
        // 
    }


}
