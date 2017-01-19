package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.strongback.Strongback;
import org.strongback.components.Solenoid;
import org.strongback.hardware.Hardware;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.chassis.OctocanumGearbox;

public class Robot extends IterativeRobot {

    private OctocanumDrive octocanumDrive;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        OctocanumGearbox frontLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT));
        OctocanumGearbox frontRight = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT));
        OctocanumGearbox rearLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT));
        OctocanumGearbox rearRight = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT));

        Solenoid shifter = Hardware.Solenoids.doubleSolenoid(Constants.GEARBOX_SHIFTER,0, Solenoid.Direction.RETRACTING);

        octocanumDrive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, shifter);

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

    }

    @Override
    public void disabledInit() {
        Strongback.disable();
    }

    @Override
    public void disabledPeriodic() {}
}
