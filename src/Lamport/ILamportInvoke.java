package Lamport;

import java.util.UUID;

public interface ILamportInvoke {
    void send(UUID target, String request);
}
