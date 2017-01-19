package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.Relay;
import org.strongback.components.Solenoid;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.TankDrive;
import org.strongback.hardware.Hardware;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.chassis.OctocanumGearbox;

public class Robot extends IterativeRobot {

    private OctocanumDrive drive;
    private FlightStick driveJoy0, driveJoy1, mashJoy;
    private Relay relay;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        OctocanumGearbox frontLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT)),
                frontRight = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT)),
                rearLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT)),
                rearRight = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT));

        Solenoid shift = Hardware.Solenoids.doubleSolenoid(Constants.SHIFTER_EXTEND, Constants.SHIFTER_RETRACT, Solenoid.Direction.EXTENDING);

        drive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, shift);

        relay = Hardware.Solenoids.relay(0);

        driveJoy0 = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
        driveJoy1 = Hardware.HumanInterfaceDevices.logitechAttack3D(1);
        mashJoy = Hardware.HumanInterfaceDevices.logitechAttack3D(2);

        Strongback.switchReactor().onTriggered(mashJoy.getButton(0),
                () -> {
                    if (relay.isOff())
                        relay.on();
                    else
                        relay.off();

                });
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
        if(driveJoy0.getThumb().isTriggered()||driveJoy1.getThumb().isTriggered())
            drive.shift();
        if(drive.getDriveMode() == OctocanumDrive.DriveMode.TRACTION)
            drive.drive(driveJoy0.getPitch().read(), 0, driveJoy1.getPitch().read(), 0);
        else {
            //TODO add mecanum drive code
        }
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
    }

    @Override
    public void disabledPeriodic() {}
}
