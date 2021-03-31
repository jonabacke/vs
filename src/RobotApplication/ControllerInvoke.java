package RobotApplication;

import Config.ConfigFile;
import Config.NetworkTuple;
import Middleware.Middleware;

import java.util.Map;
import java.util.UUID;

public class ControllerInvoke implements IControllerInvoke {

    private final Middleware middleware;

    public ControllerInvoke(Middleware middleware) {
        this.middleware = middleware;
    }

    @Override
    public void setPartner(Map<UUID, NetworkTuple> partner) {
        this.middleware.setPartner(partner);
    }

    @Override
    public void gotError(UUID targetUUID, UUID sendUUID) {
        if (targetUUID == null) throw new IllegalArgumentException();
        this.middleware.invoke(targetUUID, Controller.class, ConfigFile.ERROR_RECEIVE, new String[]{sendUUID.toString()}, true);
    }

}
