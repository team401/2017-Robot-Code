package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.AutoTestMode;
import org.team401.robot.auto.modes.CalibrateTurretMode;
import org.team401.robot.camera.Camera;
import org.team401.robot.subsystems.*;
import org.team401.robot.loops.LoopManager;
import org.team401.robot.sensors.Lidar;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Robot extends IterativeRobot {

    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private Camera camera;

    private AutoModeExecuter autoExecutor;
    private LoopManager loopManager;

    private static VisionDataStream visionDataStream = new VisionDataStream("10.4.1.17", 5801);
    private static Turret turret;

    //@Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);
        visionDataStream.start();

        Solenoid compressorFan = new Solenoid(Constants.COMPRESSOR_FAN);
        compressorFan.set(true);

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        // turret stuff
        Solenoid turretHood = new Solenoid(Constants.TURRET_HOOD);
        Solenoid ledRing = new Solenoid(Constants.TURRET_LED_RING);
        Lidar lidar = new Lidar(I2C.Port.kMXP, Lidar.Hardware.LIDARLITE_V3);
        lidar.start();
        turret = new Turret(lidar, new CANTalon(Constants.TURRET_ROTATOR), new CANTalon(Constants.TURRET_SHOOTER_MASTER),
                new CANTalon(Constants.TURRET_SHOOTER_SLAVE), new CANTalon(Constants.TURRET_FEEDER), turretHood, ledRing,
                masherJoystick.getButton(Constants.BUTTON_SHOOT_FUEL), masherJoystick.getYaw(), masherJoystick.getThrottle());

        //camera = new Camera(640, 480, 10);

        SwitchReactor switchReactor = Strongback.switchReactor();

        // drive
        switchReactor.onTriggered(driveJoystickLeft.getButton(Constants.BUTTON_SHIFT),
                () -> OctocanumDrive.INSTANCE.shift());
        switchReactor.onTriggered(driveJoystickLeft.getButton(Constants.BUTTON_TOGGLE_GYRO),
                () -> OctocanumDrive.INSTANCE.setNewHeadingSetpoint());
        switchReactor.onUntriggered(driveJoystickLeft.getButton(Constants.BUTTON_TOGGLE_GYRO),
                () -> OctocanumDrive.INSTANCE.resetHeadingSetpoint());
        // camera switching
        /*switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_SWITCH_CAMERA),
                () -> camera.switchCamera());*/
        // collection
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_ARM_DROP),
                () -> {
                    if (Intake.INSTANCE.isArmDown())
                        Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_UP);
                    else
                        Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_DOWN);
                });
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_TOGGLE_COL),
                () -> {
                    if (Intake.INSTANCE.getCurrentState() != Intake.IntakeState.ENABLED)
                        Intake.INSTANCE.setWantedState(Intake.IntakeState.ENABLED);
                    else
                        Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_DOWN);
                });
        // climbing
        // scoring
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_GEAR),
                () -> {
                    GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.OPEN);
                });
        switchReactor.onUntriggered(driveJoystickRight.getButton(Constants.BUTTON_GEAR),
                () -> {
                    GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.TOWER_OUT);
                });
        // tower
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_EXTEND_TOWER),
                () -> {
                    if (GearHolder.INSTANCE.getCurrentState() != GearHolder.GearHolderState.TOWER_IN)
                        GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.TOWER_IN);
                    else
                        GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.TOWER_OUT);
                });
        // turret
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_HOOD),
                () -> {
                    turret.extendHood(!turret.isHoodExtended());
                });
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_AUTO),
                () -> {
                    if (turret.getCurrentState() != Turret.TurretState.AUTO)
                        turret.setWantedState(Turret.TurretState.AUTO);
                    else {
                        turret.setWantedState(Turret.TurretState.SENTRY);
                        turret.extendHood(true);
                    }
                });
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_SENTRY),
                () -> {
                    if (turret.getCurrentState() == Turret.TurretState.MANUAL)
                        turret.setWantedState(Turret.TurretState.SENTRY);
                    else if (turret.getCurrentState().compareTo(Turret.TurretState.MANUAL) > 0)
                        turret.setWantedState(Turret.TurretState.MANUAL);
                });
        // hopper
        loopManager = new LoopManager();
        loopManager.register(OctocanumDrive.INSTANCE.getSubsystemLoop());
        loopManager.register(Intake.INSTANCE.getSubsystemLoop());
        loopManager.register(GearHolder.INSTANCE.getSubsystemLoop());
        loopManager.register(getTurret().getSubsystemLoop());
        loopManager.register(Hopper.INSTANCE.getSubsystemLoop());
        OctocanumDrive.INSTANCE.init();
    }

    //@Override
    public void autonomousInit() {
        loopManager.start();
        Strongback.start();
        autoExecutor = new AutoModeExecuter(new CalibrateTurretMode());
        autoExecutor.start();
    }

    //@Override
    public void teleopInit() {
        if (autoExecutor != null)
            autoExecutor.stop();
        loopManager.start();
        Strongback.restart();
    }

    //@Override
    public void autonomousPeriodic() {

    }

    //@Override
    public void teleopPeriodic() {
        // drive the robot, mode specific drive code is in the OctocanumDrive class
        OctocanumDrive.INSTANCE.drive(driveJoystickLeft.getPitch().read(), driveJoystickLeft.getRoll().read(),
                driveJoystickRight.getRoll().read());
    }

    //@Override
    public void disabledInit() {
        Strongback.disable();
        loopManager.stop();
    }

    //@Override
    public void disabledPeriodic() {}

    //subsystems
    public static VisionDataStream getVisionDataStream() {
        return visionDataStream;
    }

    public static Turret getTurret() {
        return turret;
    }
}
