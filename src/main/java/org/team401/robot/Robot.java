package org.team401.robot;

import edu.wpi.first.wpilibj.*;
import org.strongback.Strongback;
import org.strongback.components.ui.FlightStick;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.AutoTestMode;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.loops.LoopManager;

public class Robot extends IterativeRobot {

    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private Camera camera;

    private AutoModeExecuter autoExecutor;
    private LoopManager loopManager;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        loopManager = new LoopManager();
        loopManager.register(OctocanumDrive.INSTANCE.getDriveLoop());
        OctocanumDrive.INSTANCE.init();
    }

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void autonomousInit() {
        loopManager.start();
        Strongback.start();
        autoExecutor = new AutoModeExecuter(new AutoTestMode());
        autoExecutor.start();
    }

    @Override
    public void autonomousPeriodic() {
        OctocanumDrive.INSTANCE.printToSmartDashboard();
    }

    @Override
    public void teleopInit() {
        if (autoExecutor != null)
            autoExecutor.stop();
        loopManager.start();
        Strongback.restart();
    }

    @Override
    public void teleopPeriodic() {
        // drive the robot, mode specific drive code is in the OctocanumDrive class
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
        loopManager.stop();
    }

    @Override
    public void disabledPeriodic() {}
}
