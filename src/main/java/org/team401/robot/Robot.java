package org.team401.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.team401.lib.CrashTracker;
import org.team401.lib.Rotation2d;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.AutoModeSelector;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.actions.RotateAction;
import org.team401.robot.loops.GyroCalibrator;
import org.team401.robot.loops.SmartDashboardData;
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

	private static Turret turret = Turret.getInstance();
	private static Intake intake = Intake.INSTANCE;
	private static GearHolder gearHolder = GearHolder.INSTANCE;
	private static Hopper hopper = Hopper.INSTANCE;
	private static OctocanumDrive drive = OctocanumDrive.INSTANCE;

	private static ControlBoard controls = ControlBoard.INSTANCE;

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

			enabledLoop = new LoopManager();
			enabledLoop.register(intake.getSubsystemLoop());
			enabledLoop.register(gearHolder.getSubsystemLoop());
			enabledLoop.register(turret.getSubsystemLoop());
			enabledLoop.register(hopper.getSubsystemLoop());
			enabledLoop.register(drive.getSubsystemLoop());
			enabledLoop.register(new TurretCalibrator());
			drive.init();

			disabledLoop = new LoopManager();
			disabledLoop.register(new GyroCalibrator());
			disabledLoop.register(new TurretCalibrator());

			System.out.println("Done! Linking controls to code...");
			SwitchReactor switchReactor = Strongback.switchReactor();

			// drive
			switchReactor.onTriggered(controls.getShift(),
					() -> drive.shift());

			switchReactor.onTriggered(controls.getToggleHeading(),
					() -> drive.setNewHeadingSetpoint());
			switchReactor.onUntriggered(controls.getToggleHeading(),
					() -> drive.resetHeadingSetpoint());
			switchReactor.onTriggered(controls.getResetGyro(),
					() -> drive.getGyro().reset());

			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 0,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(0), .35, 5).asSbCommand());
			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 90,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(-55), .35, 5).asSbCommand());
			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 270,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(55), .35, 5).asSbCommand());
			// camera switching
			switchReactor.onTriggered(controls.getToggleCamera(),
					() -> visionController.toggleActiveCamera());
			// collection
			switchReactor.onTriggered(controls.getToggleIntake(),
					() -> {
						if (intake.getCurrentState() != Intake.IntakeState.ENABLED)
							intake.setWantedState(Intake.IntakeState.ENABLED);
						else
							intake.setWantedState(Intake.IntakeState.ARM_UP);
					});
			switchReactor.onTriggered(controls.getInverseHopper(),
					() -> {
						if (hopper.getCurrentState() != Hopper.HopperState.INVERTED)
							hopper.setWantedState(Hopper.HopperState.INVERTED);
						else
							hopper.setWantedState(Hopper.HopperState.OFF);
					});
			// climbing
			switchReactor.onUntriggered(controls.getClimb(),
					() ->
						intake.setWantedState(Intake.IntakeState.ARM_UP)
					);
			switchReactor.onTriggered(controls.getClimb(),
					() ->
						intake.setWantedState(Intake.IntakeState.CLIMBING)
					);
			// scoring
			switchReactor.onTriggered(controls.getToggleGear(),
					() ->
						gearHolder.setWantedState(GearHolder.GearHolderState.OPEN)
					);
			switchReactor.onUntriggered(controls.getToggleGear(),
					() ->
						gearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
					);
			// tower
			switchReactor.onTriggered(controls.getToggleTower(),
					() -> {
						if (gearHolder.getCurrentState() != GearHolder.GearHolderState.TOWER_IN)
							gearHolder.setWantedState(GearHolder.GearHolderState.TOWER_IN);
						else
							gearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT);
					});
			// turret
			switchReactor.onTriggeredSubmit(controls.getCalibrateTurret(),
					() -> new CalibrateTurretAction(Turret.TurretState.SENTRY).asSbCommand());
			switchReactor.onTriggered(controls.getToggleHood(),
					() ->
						turret.extendHood(!turret.isHoodExtended())
					);
			switchReactor.onTriggered(controls.getToggleAuto(),
					() -> {
						if (turret.getCurrentState() == Turret.TurretState.CALIBRATING)
							return;
						if (turret.getCurrentState() != Turret.TurretState.AUTO)
							turret.setWantedState(Turret.TurretState.AUTO);
						else
							turret.setWantedState(Turret.TurretState.MANUAL);
					});
			switchReactor.onTriggered(controls.getToggleSentry(),
					() -> {
						if (turret.getCurrentState() == Turret.TurretState.CALIBRATING)
							return;
						if (turret.getCurrentState().compareTo(Turret.TurretState.MANUAL) > 0)
							turret.setWantedState(Turret.TurretState.MANUAL);
						else
							turret.setWantedState(Turret.TurretState.SENTRY);
					});

			System.out.println("Done! Creating SmartDashboard interactions...");
			autoSelector = new AutoModeSelector();

			SmartDashboardData data = new SmartDashboardData();
			data.register(intake);
			data.register(gearHolder);
			data.register(turret);
			data.register(hopper);
			data.register(drive);
			enabledLoop.register(data);
			disabledLoop.register(data);

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
			drive.drive(-controls.getDrivePitch(), -controls.getDriveStrafe(), controls.getDriveRotate());
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
