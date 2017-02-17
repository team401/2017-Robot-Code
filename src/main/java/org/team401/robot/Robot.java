package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.Switch;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.commands.CalibrateTurret;
import org.team401.robot.components.Turret;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Robot extends IterativeRobot {

    private Motor turretSpinner;
    private FlightStick joysticky;
    private Switch switchy;
    private double sentryState = 1;
    private VisionDataStream visionDataStream;
    private Turret turret;

    private Thread turretThread;


    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);
        turretSpinner = Hardware.Motors.talonSRX(0);
        SmartDashboard.putNumber("allowedRange", 5);
        joysticky = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
        switchy = Hardware.Switches.normallyClosed(0);
        visionDataStream = new VisionDataStream("10.4.1.17", 5801);
        visionDataStream.start();

        Solenoid turretHood = new Solenoid(0);
        turretHood.set(false);

        turret = new Turret(visionDataStream, new CANTalon(0), new CANTalon(1),
                new CANTalon(2), new CANTalon(3), turretHood,
                Hardware.Switches.normallyClosed(Constants.TURRET_LIMIT_SWITCH),
                joysticky.getTrigger(), joysticky.getYaw());
        turretThread = new Thread(turret);
        turretThread.start();

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
        Strongback.submit(new CalibrateTurret(turret.getTurretRotator()));
    }

    @Override
    public void teleopPeriodic() {
        turret.run();
    }

    @Override
    public void disabledInit() {Strongback.disable();}

    @Override
    public void disabledPeriodic() {

    }
}