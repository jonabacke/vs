package RobotApplication;

import FindPartner.FindPartner;
import FindPartner.FindPartnerInvoke;
import Lamport.LamportInvoke;
import Lamport.LamportMutex;
import Logging.LogFormatter;
import Middleware.Middleware;
import Middleware.SkeletonStub;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.SEVERE);
        Logger.getGlobal().getParent().getHandlers()[0].setFormatter(new LogFormatter());
        new App(0);
    }

    public App(int id) {
        UUID uuid = UUID.randomUUID();
        Middleware middleware = new Middleware();

        RobotInvoke robotInvoke = new RobotInvoke(middleware);
        LamportInvoke lamportInvoke = new LamportInvoke(middleware);
        FindPartnerInvoke findPartnerInvoke = new FindPartnerInvoke(middleware);

        FindPartner findPartner = new FindPartner(middleware.getTcpServer().getIp(), middleware.getTcpServer().getPort(), findPartnerInvoke);
        LamportMutex lamportMutex = new LamportMutex(lamportInvoke);
        Robot robot = new Robot(uuid, robotInvoke, lamportMutex, findPartner);

        WeldingRobot weldingRobot = new WeldingRobot(robot);

        robot.init(weldingRobot);

        new SkeletonStub(FindPartner.class.getName(), findPartner, middleware, false);
        new SkeletonStub(Robot.class.getName(), robot, middleware, true);
        new SkeletonStub(LamportMutex.class.getName(), lamportMutex, middleware, true);

        weldingRobot.register(0);


        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}

