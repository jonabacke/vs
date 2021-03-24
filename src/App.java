import Config.NetworkTuple;
import FindPartner.FindPartner;

import java.sql.PreparedStatement;
import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        UUID uuid = UUID.randomUUID();
        TCPServer tcpServer = new TCPServer();
        System.out.println("Hello,");
        FindPartner findPartner = new FindPartner(tcpServer.getIp(), tcpServer.getPort(), uuid);
        System.out.println("World!");

        Map<UUID, NetworkTuple> partnerMap = findPartner.getPartner();
        Queue<Partner> partnerPriorityQueue = new PriorityQueue<>();
        System.out.println("Partners: " + partnerMap.size());

        for (Map.Entry<UUID, NetworkTuple> tuple : partnerMap.entrySet()) {
            System.out.println("Hallo " + tuple.getKey() + ", I am " + uuid.toString());
            partnerPriorityQueue.add(new Partner(tuple.getKey(), tuple.getValue()));
        }

        Self logicTime = new Self(tcpServer, partnerMap, uuid);
        for (int i = 0; i < 50; i++) {
            logicTime.startCircle();
            if (checkFirstThreeElements(partnerPriorityQueue, uuid)) {

                logicTime.requestToEnter();
                System.out.println("wait for Enter....");
                while (!logicTime.allowedToEnter()) {
                    sleep(1);
                }
                System.out.println("working....");
                logicTime.release();
            }
            while (logicTime.isDashed()) {
                sleep(1);
            }
            sleep(1000);
            System.out.println("new Cycle....");
        }

        sleep(1000);

        for (Partner partner : partnerPriorityQueue) {
            if (partner.uuid.equals(uuid)) {
                System.out.println("Worked: " + partner.workedCounter);
            }
        }


        System.out.println("Finish");


    }

    private static void sleep(long offset) {
        try {
            Thread.sleep((long) (Math.random() * 100) + offset);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkFirstThreeElements(Queue<Partner> partnerPriorityQueue, UUID uuid) {
        if (partnerPriorityQueue == null) throw new IllegalArgumentException();
        boolean result = false;
        int counter = 0;
        while (counter < 3) {
            Partner temp = partnerPriorityQueue.poll();
            assert temp != null;
            temp.workedCounter ++;
            if (temp.uuid.equals(uuid)) {
                result = true;
            }
            partnerPriorityQueue.add(temp);
            counter++;
        }
        return result;
    }
}


