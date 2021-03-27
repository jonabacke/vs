package RobotApplication;

public class WeldingRobot implements IWeldingRobot {

    private final Robot robot;
    private int status;

    public WeldingRobot(Robot robot) {
        this.robot = robot;
    }

    @Override
    public void welding() {

    }

    @Override
    public void register(int id) {
        robot.register(0);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
