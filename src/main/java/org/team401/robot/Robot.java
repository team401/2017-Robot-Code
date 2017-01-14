package org.team401.robot;

import com.ctre.CANTalon;
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
    private Motor collection;// This is for the collection method proposed by the Ethan Manipulators
    private final double collectSpeed = 0.5;

    CANTalon _talon;
    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        Motor leftDrive = Hardware.Motors.talon(0);
        Motor rightDrive = Hardware.Motors.talon(1);

        allDrive = new TankDrive(leftDrive, rightDrive);

        collection = Hardware.Motors.talon(2);

        joysticky = Hardware.HumanInterfaceDevices.logitechAttack3D(0);

        _talon = new CANTalon(9);
    }

    @Override
    public void autonomousInit() {
        Strongback.start();
    }

    @Override
    public void autonomousPeriodic() {
        collection.setSpeed(collectSpeed);
    }

    @Override
    public void teleopInit() {
        Strongback.restart();
    }

    @Override
    public void teleopPeriodic() {
        allDrive.arcade(joysticky.getPitch().read(), joysticky.getRoll().read());
        collection.setSpeed(collectSpeed);

        if(joysticky.getTrigger().isTriggered()){
            _talon.changeControlMode(CANTalon.TalonControlMode.Voltage);
            _talon.set(12.0 * joysticky.getPitch().read());
        }else if(!joysticky.getTrigger().isTriggered()){
         //   _talon.disable();
        }
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
        collection.setSpeed(0);
    }

    @Override
    public void disabledPeriodic() {}
}