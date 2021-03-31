package FindPartner;

import Config.NetworkTuple;

import java.util.Map;
import java.util.UUID;

public interface IFindPartner {

    /**
     * Get the available Partners
     *
     * @return partner Map with UUID as Key and a NetworkTuple for the TCP-Connection as Value
     */
    Map<UUID, NetworkTuple> getPartner(UUID uuid);

}
