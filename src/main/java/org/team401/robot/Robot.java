package org.team401.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.team401.lib.CrashTracker;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.AutoModeSelector;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.loops.GyroCalibrator;
import org.team401.robot.loops.TurretCalibrator;
import org.team401.robot.subsystems.*;
import org.team401.robot.loops.LoopManager;
import org.team401.vision.VisionDataStream.VisionDataStream;
import org.team401.vision.controller.VisionController;

public class Robot extends IterativeRobot {

    private AutoModeExecuter autoExecutor;
    private AutoModeSelector autoSelector;
    private LoopManager enabledLoop, disabledLoop;

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

            enabledLoop = new LoopManager();
            enabledLoop.register(Intake.INSTANCE.getSubsystemLoop());
            enabledLoop.register(GearHolder.INSTANCE.getSubsystemLoop());
            enabledLoop.register(turret.getSubsystemLoop());
            enabledLoop.register(Hopper.INSTANCE.getSubsystemLoop());
            enabledLoop.register(OctocanumDrive.INSTANCE.getSubsystemLoop());
            enabledLoop.register(new TurretCalibrator());
            OctocanumDrive.INSTANCE.init();

            disabledLoop = new LoopManager();
            disabledLoop.register(new GyroCalibrator());

            System.out.println("Done! Linking controls to code...");
            SwitchReactor switchReactor = Strongback.switchReactor();

            // drive
            switchReactor.onTriggered(ControlBoard.INSTANCE.getShift(),
                    () -> OctocanumDrive.INSTANCE.shift());
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleHeading(),

                    () -> OctocanumDrive.INSTANCE.setNewHeadingSetpoint());
            switchReactor.onUntriggered(ControlBoard.INSTANCE.getToggleHeading(),
                    () -> OctocanumDrive.INSTANCE.resetHeadingSetpoint());
            switchReactor.onTriggered(ControlBoard.INSTANCE.getResetGyro(),
                    () -> OctocanumDrive.INSTANCE.getGyro().reset());
            // camera switching
            switchReactor.onTriggered(ControlBoard.INSTANCE.getToggleCamera(),
                    () -> visionController.toggleActiveCamera());
            // collection
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
                            turret.setWantedState(Turret.TurretState.SENTRY);
                        } else {
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
            enabledLoop.start();
            disabledLoop.stop();
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
            enabledLoop.start();
            disabledLoop.stop();
            Strongback.restart();
            if (autoExecutor != null)
                autoExecutor.stop();
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
            enabledLoop.stop();
            disabledLoop.start();
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
