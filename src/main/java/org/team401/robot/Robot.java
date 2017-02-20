package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.AutoTestMode;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.commands.ShiftDriveMode;
import org.team401.robot.components.CollectionGearbox;
import org.team401.robot.components.Turret;
import org.team401.robot.loops.LoopManager;
import org.team401.robot.sensors.Lidar;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Robot extends IterativeRobot {

    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private VisionDataStream visionDataStream;
    private Camera camera;

    private Turret turret;
    private Thread turretThread;

    private AutoModeExecuter autoExecutor;
    private LoopManager loopManager;


    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);
        visionDataStream = new VisionDataStream("10.4.1.17", 5801);
        visionDataStream.start();

        CollectionGearbox collectionGearbox = new CollectionGearbox(
                Hardware.Motors.victorSP(Constants.COL_PRO_1),
                Hardware.Motors.victorSP(Constants.COL_PRO_2),
                Hardware.Motors.victorSP(Constants.COL_PRO_3)
        );

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        // turret stuff
        Solenoid turretHood = new Solenoid(Constants.TURRET_HOOD);
        Solenoid ledRing = new Solenoid(Constants.TURRET_LED_RING);
        Lidar lidar = new Lidar(I2C.Port.kMXP, Lidar.Hardware.LIDARLITE_V3);
        lidar.start();
        turret = new Turret(visionDataStream, lidar, new CANTalon(Constants.TURRET_ROTATOR), new CANTalon(Constants.TURRET_SHOOTER_LEFT),
                new CANTalon(Constants.TURRET_SHOOTER_RIGHT), new CANTalon(Constants.TURRET_FEEDER), turretHood, ledRing,
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
        switchReactor.onTriggered(driveJoystickLeft.getTrigger(), OctocanumDrive.INSTANCE::shift);
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
                    if (collectionGearbox.isRunning())
                        collectionGearbox.setSpeed(0);
                    else
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
                    if (turret.isSentryEnabled()) {
                        turret.enableSentry(false);
                        SmartDashboard.putBoolean("Auto Shooting Enabled", turret.isAutoShootingEnabled());
                    }
                    SmartDashboard.putBoolean("Sentry Mode Enabled", turret.isSentryEnabled());
                });
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
        OctocanumDrive.INSTANCE.drive(driveJoystickLeft.getPitch().read(), driveJoystickLeft.getRoll().read(),
                driveJoystickRight.getPitch().read(), driveJoystickRight.getRoll().read());
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
        loopManager.stop();
    }

    @Override
    public void disabledPeriodic() {}
}
