import FindPartner.FindPartner;

import java.util.Random;
import java.util.UUID;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        UUID uuid = UUID.randomUUID();
        Self logicTime = new Self();
        FindPartner findPartner = new FindPartner("localhost", 9000, uuid);

    }
}
