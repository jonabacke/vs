package RobotApplication;

import Logging.LogFormatter;
import Middleware.Middleware;
import Middleware.SkeletonStub;
import RobotApplication.Robot;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppWeldingRobotFactory {
    public static void main(String[] args) {
        Logger.getGlobal().getParent().getHandlers()[0].setLevel(Level.FINEST);
        Logger.getGlobal().getParent().getHandlers()[0].setFormatter(new LogFormatter());
        new AppWeldingRobotFactory();
    }

    public AppWeldingRobotFactory() {
        UUID uuid = UUID.randomUUID();
        Middleware middleware = new Middleware();
        RobotInvoke robotInvoke = new RobotInvoke(middleware);
        Robot robot = new Robot(uuid, robotInvoke);
        WeldingRobot weldingRobot = new WeldingRobot(robot);
        robot.init(weldingRobot);
        new SkeletonStub(Robot.class.getName(), robot, middleware);
        weldingRobot.register(0);






    }


}


