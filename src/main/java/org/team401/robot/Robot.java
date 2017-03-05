package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.team401.lib.CrashTracker;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.CalibrateTurretMode;
import org.team401.robot.auto.modes.ForwardGearAuto;
import org.team401.robot.camera.Camera;
import org.team401.robot.subsystems.*;
import org.team401.robot.loops.LoopManager;
import org.team401.robot.sensors.Lidar;
import org.team401.vision.VisionDataStream.VisionDataStream;
import org.team401.vision.controller.VisionController;
import org.team401.vision.controller.preset.StreamOperations;

public class Robot extends IterativeRobot {

    private Camera camera;

    private AutoModeExecuter autoExecutor;
    private LoopManager loopManager;

    private static VisionDataStream visionDataStream = new VisionDataStream("10.4.1.17", 5801);
    private static VisionController visionController = new VisionController("10.4.1.17", 5803);
    private static Turret turret;

    //@Override
    public void robotInit() {
        CrashTracker.INSTANCE.logRobotInit();
        try {
            visionDataStream.start();
            visionController.start();

            Solenoid compressorFan = new Solenoid(Constants.COMPRESSOR_FAN);
            compressorFan.set(true);

            // turret stuff
            Solenoid turretHood = new Solenoid(Constants.TURRET_HOOD);
            Solenoid ledRing = new Solenoid(Constants.TURRET_LED_RING);
            Lidar lidar = new Lidar(I2C.Port.kMXP, Lidar.Hardware.LIDARLITE_V3);
            lidar.start();
            turret = new Turret(lidar, new CANTalon(Constants.TURRET_ROTATOR), new CANTalon(Constants.TURRET_SHOOTER_MASTER),
                    new CANTalon(Constants.TURRET_SHOOTER_SLAVE), new CANTalon(Constants.TURRET_FEEDER), turretHood, ledRing);

            //camera = new Camera(640, 480, 10);
            CameraServer.getInstance().startAutomaticCapture();

            SwitchReactor switchReactor = Strongback.switchReactor();

            // drive
            switchReactor.onTriggered(ControlBoard.INSTANCE.getShift(),
                    () -> OctocanumDrive.INSTANCE.shift());
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleHeading(),

                    () -> OctocanumDrive.INSTANCE.setNewHeadingSetpoint());
            switchReactor.onUntriggered(ControlBoard.INSTANCE.getToggleHeading(),
                    () -> OctocanumDrive.INSTANCE.resetHeadingSetpoint());
            // camera switching
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleCamera(),
                () -> StreamOperations.setGoalCameraStream(visionController));
            // collection
            switchReactor.onTriggered(ControlBoard.INSTANCE.getIntakeDrop(),
                    () -> {
                        if (Intake.INSTANCE.isArmDown())
                            Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_UP);
                        else
                            Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_DOWN);
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleIntake(),
                    () -> {
                        if (Intake.INSTANCE.getCurrentState() != Intake.IntakeState.ENABLED)
                            Intake.INSTANCE.setWantedState(Intake.IntakeState.ENABLED);
                        else
                            Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_DOWN);
                    });
            // climbing
            switchReactor.onUntriggered(ControlBoard.INSTANCE.getClimb(),
                    () -> {
                        Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_UP);
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getClimb(),
                    () -> {
                        Intake.INSTANCE.setWantedState(Intake.IntakeState.CLIMBING);
                    });
            // scoring
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleGear(),
                    () -> {
                        GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.OPEN);
                    });
            switchReactor.onUntriggered(ControlBoard.INSTANCE.getToggleGear(),
                    () -> {
                        GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.TOWER_OUT);
                    });
            // tower
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleTower(),
                    () -> {
                        if (GearHolder.INSTANCE.getCurrentState() != GearHolder.GearHolderState.TOWER_IN)
                            GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.TOWER_IN);
                        else
                            GearHolder.INSTANCE.setWantedState(GearHolder.GearHolderState.TOWER_OUT);
                    });
            // turret
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleHood(),
                    () -> {
                        turret.extendHood(!turret.isHoodExtended());
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleAuto(),
                    () -> {
                        if (turret.getCurrentState() == Turret.TurretState.CALIBRATING)
                            return;
                        if (turret.getCurrentState() != Turret.TurretState.AUTO)
                            turret.setWantedState(Turret.TurretState.AUTO);
                        else {
                            turret.setWantedState(Turret.TurretState.SENTRY);
                        }
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleSentry(),
                    () -> {
                        if (turret.getCurrentState() == Turret.TurretState.CALIBRATING)
                            return;
                        if (turret.getCurrentState() == Turret.TurretState.AUTO ||
                                turret.getCurrentState() == Turret.TurretState.MANUAL)
                            turret.setWantedState(Turret.TurretState.SENTRY);
                        else
                            turret.setWantedState(Turret.TurretState.MANUAL);
                    });
            // hopper
            loopManager = new LoopManager();
            loopManager.register(Intake.INSTANCE.getSubsystemLoop());
            loopManager.register(GearHolder.INSTANCE.getSubsystemLoop());
            loopManager.register(getTurret().getSubsystemLoop());
            loopManager.register(Hopper.INSTANCE.getSubsystemLoop());
            loopManager.register(OctocanumDrive.INSTANCE.getSubsystemLoop());
            OctocanumDrive.INSTANCE.init();
        } catch (Throwable t) {
            CrashTracker.INSTANCE.logThrowableCrash(t);
        }
    }

    //@Override
    public void autonomousInit() {
        try {
            CrashTracker.INSTANCE.logAutoInit();
            loopManager.start();
            Strongback.restart();
            autoExecutor = new AutoModeExecuter(new ForwardGearAuto());
            autoExecutor.start();
        } catch (Throwable t) {
            CrashTracker.INSTANCE.logThrowableCrash(t);
        }
    }

    //@Override
    public void teleopInit() {
        try {
            CrashTracker.INSTANCE.logTeleopInit();
            if (autoExecutor != null)
                autoExecutor.stop();
            loopManager.start();
            Strongback.restart();
        } catch (Throwable t) {
            CrashTracker.INSTANCE.logThrowableCrash(t);
        }
    }

    //@Override
    public void autonomousPeriodic() {
        try {

        } catch (Throwable t) {
            CrashTracker.INSTANCE.logThrowableCrash(t);
        }
    }

    //@Override
    public void teleopPeriodic() {
        try {
            // drive the robot, mode specific drive code is in the OctocanumDrive class
            if (!ControlBoard.INSTANCE.getLeftDriveJoystick().getButton(5).isTriggered())
                OctocanumDrive.INSTANCE.drive(ControlBoard.INSTANCE.getDrivePitch(), ControlBoard.INSTANCE.getDriveStrafe(),
                        ControlBoard.INSTANCE.getDriveRotate());
            else
                OctocanumDrive.INSTANCE.drive(-ControlBoard.INSTANCE.getDrivePitch(), ControlBoard.INSTANCE.getDriveStrafe(),
                        ControlBoard.INSTANCE.getDriveRotate());
        } catch (Throwable t) {
            CrashTracker.INSTANCE.logThrowableCrash(t);
        }
    }

    //@Override
    public void disabledInit() {
        try {
            CrashTracker.INSTANCE.logDisabledInit();
            Strongback.disable();
            loopManager.stop();

        } catch (Throwable t) {
            CrashTracker.INSTANCE.logThrowableCrash(t);
        }
    }

    //@Override
    public void disabledPeriodic() {}

    //subsystems
    public static VisionDataStream getVisionDataStream() {
        return visionDataStream;
    }

    public static VisionController getVisionController() {
        return visionController;
    }

    public static Turret getTurret() {
        return turret;
    }
}
