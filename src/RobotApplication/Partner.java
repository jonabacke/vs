package RobotApplication;

import Config.NetworkTuple;

import java.util.UUID;

public class Partner implements Comparable<Partner>{

    private final UUID uuid;
    private final NetworkTuple networkTuple;
    private int workedCounter = 0;

    public Partner(UUID uuid, NetworkTuple networkTuple) {
        this.uuid = uuid;
        this.networkTuple = networkTuple;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getWorkedCounter() {
        return workedCounter;
    }

    public void setWorkedCounter(int workedCounter) {
        this.workedCounter = workedCounter;
    }

    @Override
    public int compareTo(Partner partner) {
        if (partner == null) throw new IllegalArgumentException();
        if (this.workedCounter == partner.workedCounter) {
        return this.uuid.compareTo(partner.uuid);
        } else {
            return Integer.compare(this.workedCounter, partner.workedCounter);
        }
    }

    @Override
    public String toString() {
        return "Robot.Partner{" +
                "uuid=" + uuid.toString() +
                ", networkTuple=" + networkTuple.toString() +
                ", workedCounter=" + workedCounter +
                '}';
    }
}
