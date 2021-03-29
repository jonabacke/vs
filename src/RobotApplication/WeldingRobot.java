package RobotApplication;

public class WeldingRobot implements IWeldingRobot {

    private final Controller controller;

    public WeldingRobot(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void welding() {

    }

    @Override
    public void register(int id) {
        controller.register(0);
    }

    @Override
    public void setStatus(int status) {
    }
}
