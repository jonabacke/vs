package RobotApplication;

import Config.ConfigFile;
import Config.NetworkTuple;
import Middleware.Middleware;

import java.util.Map;
import java.util.UUID;

public class RobotInvoke {

    private final Middleware middleware;

    public RobotInvoke(Middleware middleware) {
        this.middleware = middleware;
    }

    public void setPartner(Map<UUID, NetworkTuple> partner) {
        this.middleware.setPartner(partner);
    }

    public void gotError(UUID targetUUID, UUID sendUUID) {
        this.middleware.invoke(targetUUID, Controller.class, ConfigFile.ERROR_RECEIVE, new String[]{sendUUID.toString()}, true);
    }

}
