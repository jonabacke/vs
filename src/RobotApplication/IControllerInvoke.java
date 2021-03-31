package RobotApplication;

import Config.NetworkTuple;

import java.util.Map;
import java.util.UUID;

public interface IControllerInvoke {
    void gotError(UUID targetUUID, UUID sendUUID);

    void setPartner(Map<UUID, NetworkTuple> partner);
}
