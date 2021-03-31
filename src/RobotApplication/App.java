package RobotApplication;

import FindPartner.FindPartner;
import FindPartner.FindPartnerInvoke;
import FindPartner.IFindPartner;
import FindPartner.IFindPartnerInvoke;
import Lamport.ILamportInvoke;
import Lamport.ILamportMutex;
import Lamport.LamportInvoke;
import Lamport.LamportMutex;
import Logging.LogFormatter;
import Middleware.Middleware;
import Middleware.SkeletonStub;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    public App(int id) {
        UUID uuid = UUID.randomUUID();
        Middleware middleware = new Middleware();

        IControllerInvoke controllerInvoke = new ControllerInvoke(middleware);
        ILamportInvoke lamportInvoke = new LamportInvoke(middleware);
        IFindPartnerInvoke findPartnerInvoke = new FindPartnerInvoke(middleware);

        IFindPartner findPartner = new FindPartner(middleware.getTcpServer().getIp(), middleware.getTcpServer().getPort(), findPartnerInvoke);
        ILamportMutex lamportMutex = new LamportMutex(lamportInvoke);
        Controller controller = new Controller(uuid, controllerInvoke, lamportMutex, findPartner);

        WeldingRobot weldingRobotInvoke = new WeldingRobot(controller);


        controller.init(weldingRobotInvoke);

        new SkeletonStub(FindPartner.class.getName(), findPartner, middleware, false);
        new SkeletonStub(Controller.class.getName(), controller, middleware, true);
        new SkeletonStub(LamportMutex.class.getName(), lamportMutex, middleware, true);

        weldingRobotInvoke.register(id);


    }

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.SEVERE);
        Logger.getGlobal().getParent().getHandlers()[0].setFormatter(new LogFormatter());
        int id = 0;
        if (args.length > 0 && args[0] != null) {
            id = Integer.parseInt(args[0]);
        }
        new App(id);
    }


}


