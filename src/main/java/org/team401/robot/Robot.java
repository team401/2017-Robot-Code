package org.team401.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.team401.lib.CrashTracker;
import org.team401.lib.Rotation2d;
import org.team401.robot.auto.AutoModeExecutor;
import org.team401.robot.auto.AutoModeSelector;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.actions.RotateAction;
import org.team401.robot.loops.GyroCalibrator;
import org.team401.robot.loops.LedManager;
import org.team401.robot.loops.SmartDashboardData;
import org.team401.robot.loops.TurretCalibrator;
import org.team401.robot.subsystems.*;
import org.team401.lib.LoopManager;
import org.team401.vision.VisionDataStream.VisionDataStream;
import org.team401.vision.controller.VisionController;

public class Robot extends IterativeRobot {

	private AutoModeExecutor autoExecutor;
	private AutoModeSelector autoSelector;
	private LoopManager enabledLoop, disabledLoop;

	private static VisionDataStream visionDataStream;
	private static VisionController visionController;

	private static Turret turret = Turret.INSTANCE;
	private static Intake intake = Intake.INSTANCE;
	private static Tower tower = Tower.INSTANCE;
	private static GearHolder gearHolder = GearHolder.INSTANCE;
	private static Hopper hopper = Hopper.INSTANCE;
	private static OctocanumDrive drive = OctocanumDrive.INSTANCE;
	private static Flywheel flywheel = Flywheel.INSTANCE;

	private static ControlBoard controls = ControlBoard.INSTANCE;

	private static PowerDistributionPanel pdp;
	private static Compressor compressor;

	public void robotInit() {
		CrashTracker.INSTANCE.logRobotInit();
		try {
			System.out.print("Vision network starting... ");
			visionDataStream = new VisionDataStream("10.4.1.17", 5801);
			visionDataStream.start();
			visionController = new VisionController("10.4.1.17", 5803);
			visionController.start();

			System.out.print("Done!\nInitializing subsystems... ");
			Solenoid compressorFan = new Solenoid(Constants.COMPRESSOR_FAN);
			compressorFan.set(true);

			enabledLoop = new LoopManager();
			enabledLoop.register(intake.getSubsystemLoop());
			enabledLoop.register(gearHolder.getSubsystemLoop());
			enabledLoop.register(tower.getSubsystemLoop());
			enabledLoop.register(turret.getSubsystemLoop());
			enabledLoop.register(hopper.getSubsystemLoop());
			enabledLoop.register(drive.getSubsystemLoop());
			enabledLoop.register(flywheel.getSubsystemLoop());
			enabledLoop.register(new TurretCalibrator());
			drive.init();

			disabledLoop = new LoopManager();
			disabledLoop.register(new GyroCalibrator());
			disabledLoop.register(new TurretCalibrator());

			LedManager leds = new LedManager();
			enabledLoop.register(leds);
			disabledLoop.register(leds);

			pdp = new PowerDistributionPanel();
			compressor = new Compressor();

			System.out.print("Done!\nLinking controls to code... ");
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
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(-50), .35, 5).asSbCommand());
			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 270,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(50), .35, 5).asSbCommand());
			// camera switching
			switchReactor.onTriggered(controls.getToggleCamera(),
					() -> visionController.toggleActiveCamera());
			// collection
			switchReactor.onTriggered(controls.getToggleIntake(),
					() -> intake.setWantedState(Intake.IntakeState.ENABLED));
			switchReactor.onUntriggered(controls.getToggleIntake(),
					() -> intake.setWantedState(Intake.IntakeState.DISABLED));

			switchReactor.onTriggered(controls.getInverseHopper(),
					() -> {
						if (hopper.getCurrentState() != Hopper.HopperState.INVERTED)
							hopper.setWantedState(Hopper.HopperState.INVERTED);
						else
							hopper.setWantedState(Hopper.HopperState.OFF);
					});
			// climbing
			// scoring
			switchReactor.onTriggered(controls.getGearIntake(),
					() -> {
						gearHolder.setWantedState(GearHolder.GearHolderState.INTAKE);
						tower.setWantedState(Tower.TowerState.TOWER_IN);
						drive.shift(OctocanumDrive.DriveMode.TRACTION);
					});
			switchReactor.onUntriggered(controls.getGearIntake(),
					() -> {
						gearHolder.setWantedState(GearHolder.GearHolderState.CLOSED);
						drive.shift(OctocanumDrive.DriveMode.MECANUM);
					});
			switchReactor.onTriggered(controls.getGearOut(),
					() -> gearHolder.setWantedState(GearHolder.GearHolderState.PUSH_OUT));
			switchReactor.onUntriggered(controls.getGearOut(),
					() -> gearHolder.setWantedState(GearHolder.GearHolderState.CLOSED));

			// tower
			switchReactor.onTriggered(controls.getToggleTower(),
					() -> {
						if (tower.getCurrentState() != Tower.TowerState.TOWER_IN)
							tower.setWantedState(Tower.TowerState.TOWER_IN);
						else
							tower.setWantedState(Tower.TowerState.TOWER_OUT);
					});
			// turret
			switchReactor.onTriggeredSubmit(controls.getCalibrateTurret(),
					() -> new CalibrateTurretAction(Turret.TurretState.SENTRY).asSbCommand());
            switchReactor.onTriggered(controls.getToggleHood(),
                    () -> turret.extendHood(!turret.isHoodExtended()));
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
			switchReactor.onTriggered(controls.getInverseKicker(),
					() -> tower.setWantedState(Tower.TowerState.KICKER_INVERTED));
			switchReactor.onUntriggered(controls.getInverseKicker(),
					() -> tower.setWantedState(Tower.TowerState.TOWER_OUT));

			System.out.print("Done!\nCreating SmartDashboard interactions... ");
			autoSelector = new AutoModeSelector();

			SmartDashboardData data = new SmartDashboardData();
			data.register(intake);
			data.register(gearHolder);
			data.register(turret);
			data.register(hopper);
			data.register(drive);
			data.register(flywheel);
			enabledLoop.register(data);
			disabledLoop.register(data);

			System.out.print("Done!\nSetting cameras to stream mode... ");
			visionController.setCameraMode(VisionController.Camera.GEAR, VisionController.CameraMode.STREAMING);
			visionController.setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.STREAMING);
			System.out.println("Done!\nRobot is ready for match!");
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

	public void autonomousInit() {
		try {
			CrashTracker.INSTANCE.logAutoInit();
			disabledLoop.stop();
			enabledLoop.start();
			Strongback.restart();
			autoExecutor = new AutoModeExecutor(autoSelector.getAutoMode());
			autoExecutor.start();
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

	public void teleopInit() {
		try {
			CrashTracker.INSTANCE.logTeleopInit();
			disabledLoop.stop();
			enabledLoop.start();
			Strongback.restart();
			if (autoExecutor != null)
				autoExecutor.stop();
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

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

	public void teleopPeriodic() {
		try {
			// drive the robot, mode specific drive code is in the OctocanumDrive class
			drive.drive(-controls.getDrivePitch(), -controls.getDriveStrafe(), controls.getDriveRotate());
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

	//subsystems
	public static VisionDataStream getVisionDataStream() {
		return visionDataStream;
	}

	public static VisionController getVisionController() {
		return visionController;
	}

	public static PowerDistributionPanel getPowerDistributionPanel() {
		return pdp;
	}

	public static Compressor getCompressor() {
		return compressor;
	}
}
