package org.team401.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.commands.CalibrateTurret;
import org.team401.robot.commands.ShiftDriveMode;
import org.team401.robot.components.CollectionGearbox;
import org.team401.robot.components.OctocanumGearbox;
import org.team401.robot.components.Turret;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Robot extends IterativeRobot {

    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private VisionDataStream visionDataStream;
    private Camera camera;

    private OctocanumDrive octocanumDrive;
    private Turret turret;

    private Thread turretThread;


    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);
        visionDataStream = new VisionDataStream("10.4.1.17", 5801);
        visionDataStream.start();

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        // drive stuff
        OctocanumGearbox frontLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT));
        OctocanumGearbox frontRight = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT));
        OctocanumGearbox rearLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT));
        OctocanumGearbox rearRight = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT));
        ADIS16448_IMU g = new ADIS16448_IMU();
        g.calibrate();
        octocanumDrive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, new Solenoid(Constants.GEARBOX_SHIFTER), g);

        // collection stuff
        CollectionGearbox collectionGearbox = new CollectionGearbox(
                Hardware.Motors.victorSP(Constants.COL_PRO_1),
                Hardware.Motors.victorSP(Constants.COL_PRO_2),
                Hardware.Motors.victorSP(Constants.COL_PRO_3)
        );

        // turret stuff
        Solenoid turretHood = new Solenoid(Constants.TURRET_HOOD);
        turretHood.set(false);
        turret = new Turret(visionDataStream, new CANTalon(Constants.TURRET_ROTATOR), new CANTalon(Constants.TURRET_SHOOTER_LEFT),
                new CANTalon(Constants.TURRET_SHOOTER_RIGHT), new CANTalon(Constants.TURRET_FEEDER), turretHood,
                Hardware.Switches.normallyClosed(Constants.TURRET_LIMIT_SWITCH),
                masherJoystick.getButton(Constants.BUTTON_SHOOT_FUEL), masherJoystick.getYaw(), masherJoystick.getThrottle());
        turretThread = new Thread(turret);
        turretThread.start();

        camera = new Camera(640, 480, 10);

        Solenoid collectionExtender = new Solenoid(Constants.COL_EXTENDER);
        collectionExtender.set(false);
        Solenoid turretExtender = new Solenoid(Constants.TURRET_SHIFTER);
        turretExtender.set(false);

        SwitchReactor switchReactor = Strongback.switchReactor();

        // shift drive modes
        switchReactor.onTriggeredSubmit(driveJoystickLeft.getTrigger(),
                () -> new ShiftDriveMode(octocanumDrive));
        // camera switching
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_SWITCH_CAMERA),
                () -> camera.switchCamera());
        // collection
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_COL_DROP),
                () -> {
                    collectionExtender.set(!collectionExtender.get());
                    SmartDashboard.putBoolean("Collection Down", collectionExtender.get());
                });
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_COL_TOGGLE),
                () -> {
                    if (collectionGearbox.isRunning()) {
                        collectionGearbox.setSpeed(0);
                    } else if (!collectionGearbox.isRunning() && !collectionExtender.get()) {
                        collectionExtender.set(true);
                        SmartDashboard.putBoolean("Collection Down", collectionExtender.get());
                        collectionGearbox.setSpeed(1);
                    } else
                        collectionGearbox.setSpeed(1);
                    SmartDashboard.putBoolean("Collection Enabled", collectionGearbox.isRunning());
                });
        // turret
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_EXTEND_TURRET),
                () -> {
                    turretExtender.set(!turretExtender.get());
                    SmartDashboard.putBoolean("Turret Extended", turretExtender.get());
                });
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_HOOD),
                () -> {
                    turret.extendHood(!turret.isHoodExtended());
                    SmartDashboard.putBoolean("Hood Extended", turret.isHoodExtended());
                });
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_AUTO),
                () -> {
                    turret.enableAutoShooting(!turret.isAutoShootingEnabled());
                    if (turret.isAutoShootingEnabled()) {
                        turret.enableSentry(true);
                        SmartDashboard.putBoolean("Sentry Mode Enabled", turret.isSentryEnabled());
                    }
                    SmartDashboard.putBoolean("Auto Shooting Enabled", turret.isAutoShootingEnabled());
                });
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_SENTRY),
                () -> {
                    turret.enableSentry(!turret.isSentryEnabled());
                    SmartDashboard.putBoolean("Sentry Mode Enabled", turret.isSentryEnabled());
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