package RobotApplication;

public class WeldingRobot implements IWedingRobotInvoke {

    private final Controller controller;

    public WeldingRobot(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void welding() {

    }

    public void register(int id) {
        controller.register(id);
    }

    @Override
    public void setStatus(int status) {
    }
}
