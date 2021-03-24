import Config.NetworkTuple;

import java.util.UUID;

public class Partner implements Comparable<Partner>{

    UUID uuid = null;
    NetworkTuple networkTuple = null;
    int workedCounter = 0;

    public Partner(UUID uuid, NetworkTuple networkTuple) {
        this.uuid = uuid;
        this.networkTuple = networkTuple;
    }

    public UUID getUuid() {
        return uuid;
    }

    public NetworkTuple getNetworkTuple() {
        return networkTuple;
    }

    public int getWorkedCounter() {
        return workedCounter;
    }

    @Override
    public int compareTo(Partner partner) {
        if (partner == null) throw new IllegalArgumentException();
        if (Integer.compare(this.workedCounter, partner.workedCounter) == 0) {
        return this.uuid.compareTo(partner.uuid);
        } else {
            return Integer.compare(this.workedCounter, partner.workedCounter);
        }
    }

    @Override
    public String toString() {
        return "Partner{" +
                "uuid=" + uuid.toString() +
                ", networkTuple=" + networkTuple.toString() +
                ", workedCounter=" + workedCounter +
                '}';
    }
}
