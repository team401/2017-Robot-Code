package org.team401.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.TalonSRX;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.TankDrive;
import org.strongback.hardware.Hardware;

public class Robot extends IterativeRobot {

    private TankDrive allDrive;
    private FlightStick joysticky;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        Motor leftDrive = Hardware.Motors.talon(0);
        Motor rightDrive = Hardware.Motors.talon(1).invert();

        allDrive = new TankDrive(leftDrive, rightDrive);

        joysticky = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
    }

    @Override
    public void autonomousInit() {
        Strongback.start();
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        Strongback.restart();
    }

    @Override
    public void teleopPeriodic() {
        allDrive.arcade(joysticky.getPitch().read(), joysticky.getRoll().read()*-1);
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
    }

    @Override
    public void disabledPeriodic() {}
}
