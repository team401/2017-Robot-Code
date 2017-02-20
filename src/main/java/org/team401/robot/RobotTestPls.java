package org.team401.robot;

import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.AutoTestMode;
import org.team401.robot.subsystems.OctocanumDrive;
import org.team401.robot.loops.LoopManager;

public class RobotTestPls /*extends IterativeRobot*/ {

    private AutoModeExecuter autoModeExecuter;
    private LoopManager loopManager;

    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;

    //@Override
    public void robotInit() {
        OctocanumDrive.INSTANCE.init();

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        loopManager = new LoopManager();
        loopManager.register(OctocanumDrive.INSTANCE.getDriveLoop());
    }

    //@Override
    public void autonomousInit() {
        autoModeExecuter = new AutoModeExecuter(new AutoTestMode());
        autoModeExecuter.start();
        loopManager.start();
    }

    //@Override
    public void teleopInit() {
        loopManager.start();
        OctocanumDrive.INSTANCE.setBrakeMode(true);
    }

    //@Override
    public void disabledInit() {
        loopManager.stop();
    }

    //@Override
    public void autonomousPeriodic() {

    }

    //@Override
    public void teleopPeriodic() {
        OctocanumDrive.INSTANCE.drive(driveJoystickLeft.getPitch().read(), driveJoystickLeft.getRoll().read(),
                driveJoystickRight.getRoll().read());
    }
}
