package org.team401.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.team401.lib.CrashTracker;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.AutoModeSelector;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.modes.CalibrateTurretMode;
import org.team401.robot.camera.Camera;
import org.team401.robot.subsystems.*;
import org.team401.robot.loops.LoopManager;
import org.team401.vision.VisionDataStream.VisionDataStream;
import org.team401.vision.controller.VisionController;

public class Robot /*extends IterativeRobot*/ {

    private Camera camera;

    private AutoModeExecuter autoExecutor;
    private AutoModeSelector autoSelector;
    private LoopManager loopManager;

    private static VisionDataStream visionDataStream;
    private static VisionController visionController;
    private static Turret turret;

    //@Override
    public void robotInit() {
        CrashTracker.INSTANCE.logRobotInit();
        try {
            System.out.println("Vision network starting...");
            visionDataStream = new VisionDataStream("10.4.1.17", 5801);
            visionDataStream.start();
            visionController = new VisionController("10.4.1.17", 5803);
            visionController.start();

            System.out.println("Done! Initializing subsystems...");
            Solenoid compressorFan = new Solenoid(Constants.COMPRESSOR_FAN);
            compressorFan.set(true);

            turret = Turret.getInstance();
            //camera = new Camera(640, 480, 10);

            loopManager = new LoopManager();
            loopManager.register(Intake.INSTANCE.getSubsystemLoop());
            loopManager.register(GearHolder.INSTANCE.getSubsystemLoop());
            loopManager.register(turret.getSubsystemLoop());
            loopManager.register(Hopper.INSTANCE.getSubsystemLoop());
            loopManager.register(OctocanumDrive.INSTANCE.getSubsystemLoop());
            OctocanumDrive.INSTANCE.init();

            System.out.println("Done! Linking controls to code...");
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
                    () -> visionController.toggleActiveCamera());
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
                            Intake.INSTANCE.setWantedState(Intake.IntakeState.ARM_UP);
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getInverseHopper(),
                    () -> {
                        if (Hopper.INSTANCE.getCurrentState() != Hopper.HopperState.INVERTED)
                            Hopper.INSTANCE.setWantedState(Hopper.HopperState.INVERTED);
                        else
                            Hopper.INSTANCE.setWantedState(Hopper.HopperState.OFF);
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
            switchReactor.onTriggeredSubmit(ControlBoard.INSTANCE.getCalibrateTurret(),
                    () -> new CalibrateTurretAction(Turret.TurretState.SENTRY).asSbCommand());
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleHood(),
                    () -> {
                        turret.extendHood(!turret.isHoodExtended());
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleAuto(),
                    () -> {
                        if (turret.getCurrentState() == Turret.TurretState.CALIBRATING)
                            return;
                        if (turret.getCurrentState() != Turret.TurretState.AUTO) {
                            visionController.setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.PROCESSING);
                            turret.setWantedState(Turret.TurretState.AUTO);
                        } else {
                            turret.setWantedState(Turret.TurretState.SENTRY);
                        }
                    });
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleSentry(),
                    () -> {
                        if (turret.getCurrentState() == Turret.TurretState.CALIBRATING)
                            return;
                        if (turret.getCurrentState() == Turret.TurretState.AUTO ||
                                turret.getCurrentState() == Turret.TurretState.MANUAL) {
                            visionController.setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.PROCESSING);
                            turret.setWantedState(Turret.TurretState.SENTRY);
                        } else {
                            visionController.setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.STREAMING);
                            turret.setWantedState(Turret.TurretState.MANUAL);
                        }
                    });

            System.out.println("Done! Creating SmartDashboard interactions...");
            autoSelector = new AutoModeSelector();

            System.out.println("Done! Setting cameras to stream mode...");
            visionController.setCameraMode(VisionController.Camera.GEAR, VisionController.CameraMode.STREAMING);
            visionController.setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.STREAMING);
            System.out.println("Done! Robot is ready for match!");

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
            autoExecutor = new AutoModeExecuter(autoSelector.getAutoMode());
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
            OctocanumDrive.INSTANCE.drive(-ControlBoard.INSTANCE.getDrivePitch(), -ControlBoard.INSTANCE.getDriveStrafe(),
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
}
