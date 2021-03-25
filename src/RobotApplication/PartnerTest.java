package RobotApplication;

import Config.NetworkTuple;
import RobotApplication.Partner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PartnerTest {

    Queue<Partner> partners1 = new PriorityQueue<>();
    Queue<Partner> partners2 = new PriorityQueue<>();
    Queue<Partner> partners3 = new PriorityQueue<>();
    Queue<Partner> partners4 = new PriorityQueue<>();
    Queue<Partner> partners5 = new PriorityQueue<>();
    Queue<Partner> partners6 = new PriorityQueue<>();

    @BeforeEach
    void setUp() {
        Partner [] partners = new Partner[100];
        for (int i = 0; i < 100; i++) {
            Partner partner = new Partner(UUID.randomUUID(), new NetworkTuple("asd", 123));
            partner.setWorkedCounter((int) (Math.random() * 1000));
            partners[i] = partner;
        }
        for (int i = 0; i < 100; i++) {
            partners1.add(partners[i]);
            partners2.add(partners[(i + 10) % 100]);
            partners3.add(partners[(i + 20) % 100]);
            partners4.add(partners[(i + 30) % 100]);
            partners5.add(partners[(i + 40) % 100]);
            partners6.add(partners[(i + 50) % 100]);

        }
    }

    @Test
    void compareTo() {
        while (partners1.peek() != null && partners2.peek() != null) {
            System.out.println(partners1.peek().toString());
            assertEquals(partners1.poll().toString(), Objects.requireNonNull(partners2.poll()).toString());
        }
        while (partners3.peek() != null && partners4.peek() != null) {
            assertEquals(partners3.poll().toString(), Objects.requireNonNull(partners4.poll()).toString());
        }
        while (partners5.peek() != null && partners6.peek() != null) {
            assertEquals(partners5.poll().toString(), Objects.requireNonNull(partners6.poll()).toString());
        }

        Iterator<Partner> iter1 = partners1.iterator();
        Iterator<Partner> iter2 = partners6.iterator();
        while (iter1.hasNext()){
            assertEquals(iter1.next().toString(), (iter2.next().toString()));
        }

    }
}