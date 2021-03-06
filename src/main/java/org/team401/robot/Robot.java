package org.team401.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.team401.lib.*;
import org.team401.robot.auto.AutoModeExecutor;
import org.team401.robot.auto.AutoModeSelector;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.actions.RotateAction;
import org.team401.robot.loops.GyroCalibrator;
import org.team401.robot.loops.LedManager;
import org.team401.robot.loops.TurretCalibrator;
import org.team401.robot.subsystems.*;
import org.team401.vision.controller.VisionController;

public class Robot extends IterativeRobot {

	private AutoModeExecutor autoExecutor;
	private AutoModeSelector autoSelector;
	private LoopManager enabledLoop, disabledLoop;

	private static VisionBuffer vision;

	private static Turret turret;
	private static Intake intake;
	private static Tower tower;
	private static GearHolder gearHolder;
	private static Hopper hopper;
	private static OctocanumDrive drive;
	private static Flywheel flywheel;

	private static ControlBoard controls = ControlBoard.INSTANCE;

	private static FMS fms = FMS.INSTANCE;

	private static PowerDistributionPanel pdp;
	private static Compressor compressor;

	public void robotInit() {
		CrashTracker.INSTANCE.logRobotStartup();
		try {
			System.out.print("Vision network starting... ");

            enabledLoop = new LoopManager();
			vision = VisionBuffer.INSTANCE;
			enabledLoop.register(vision.getBufferLoop());

			System.out.print("Done!\nInitializing subsystems... ");
			Solenoid compressorFan = new Solenoid(Constants.COMPRESSOR_FAN);
			compressorFan.set(true);

			intake = Intake.INSTANCE;
			gearHolder = GearHolder.INSTANCE;
			tower = Tower.INSTANCE;
			turret = Turret.INSTANCE;
			hopper = Hopper.INSTANCE;
			drive = OctocanumDrive.INSTANCE;
			flywheel = Flywheel.INSTANCE;

			enabledLoop.register(intake.getSubsystemLoop());
			enabledLoop.register(gearHolder.getSubsystemLoop());
			enabledLoop.register(tower.getSubsystemLoop());
			enabledLoop.register(turret.getSubsystemLoop());
			enabledLoop.register(hopper.getSubsystemLoop());
			enabledLoop.register(drive.getSubsystemLoop());
			enabledLoop.register(flywheel.getSubsystemLoop());
			enabledLoop.register(new TurretCalibrator());

			disabledLoop = new LoopManager();
			disabledLoop.register(new GyroCalibrator(drive.getGyro()));
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

			switchReactor.onTriggered(controls.getToggleOpenLoop(),
					() -> {
			            if (drive.getControlState() == OctocanumDrive.DriveControlState.CLOSED_LOOP)
			                drive.setControlState(OctocanumDrive.DriveControlState.OPEN_LOOP);
			            else
			                drive.setControlState(OctocanumDrive.DriveControlState.CLOSED_LOOP);
                    });
			switchReactor.onTriggered(controls.getResetGyro(),
					() -> drive.getGyro().reset());
			switchReactor.onTriggered(controls.getToggleBrake(),
                    () -> drive.setBrakeMode(!drive.getBrakeModeOn()));
			switchReactor.onTriggered(controls.getToggleHeading(),
                    () -> drive.setVelocityHeadingSetpoint(-controls.getDrivePitch()*Constants.MAX_SPEED*12, drive.getGyroAngle()));
			switchReactor.onUntriggered(controls.getToggleHeading(),
                    () -> drive.setControlState(OctocanumDrive.DriveControlState.CLOSED_LOOP));

			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 0,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(0)).asSbCommand());
			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 90,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(-50)).asSbCommand());
			switchReactor.onTriggeredSubmit(() -> controls.getGyroPadAngle().getDirection() == 270,
					() -> new RotateAction(Rotation2d.Companion.fromDegrees(50)).asSbCommand());

			switchReactor.onTriggered(controls.getToggleGearProc(),
                    () -> gearHolder.setWantedState(GearHolder.GearHolderState.GEAR_VISION));
			switchReactor.onUntriggered(controls.getToggleGearProc(),
                    () -> gearHolder.setWantedState(GearHolder.GearHolderState.CLOSED));

			// camera switching
			switchReactor.onTriggered(controls.getToggleCamera(),
					() -> vision.toggleActiveCamera());
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
					() -> new CalibrateTurretAction(Turret.TurretState.MANUAL).asSbCommand());
			switchReactor.onTriggered(controls.getDisableTurret(),
                    () -> turret.setWantedState(Turret.TurretState.DISABLED));
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

			switchReactor.onTriggered(() -> !fms.isAutonomous() && fms.getMatchTime() <= 30 && fms.getMatchTime() >= 0,
                    () -> compressor.stop());

			System.out.print("Done!\nInitializing data logging... ");

            Subsystem.Companion.getDataLoop().start();
			autoSelector = new AutoModeSelector();

			System.out.print("Done!\nSetting cameras to stream mode... ");
			vision.setGoalCameraMode(VisionController.CameraMode.STREAMING);
			vision.setGearCameraMode(VisionController.CameraMode.STREAMING);
			System.out.println("Done!\nRobot is ready for match!");
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
            System.out.println("Robot could not be started!");
            System.exit(1);
        }
        CrashTracker.INSTANCE.logRobotInit();
	}

	public void autonomousInit() {
		try {
			CrashTracker.INSTANCE.logAutoInit();
            disabledLoop.stop();
            enabledLoop.start();
            autoExecutor = new AutoModeExecutor(autoSelector.getAutoMode());
            autoExecutor.start();
			Strongback.restart();
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

	public void teleopInit() {
		try {
			CrashTracker.INSTANCE.logTeleopInit();
			disabledLoop.stop();
			enabledLoop.start();
            if (autoExecutor != null)
                autoExecutor.stop();
			Strongback.restart();
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

	public void disabledInit() {
		try {
			CrashTracker.INSTANCE.logDisabledInit();
			enabledLoop.stop();
			disabledLoop.start();
            Strongback.disable();
		} catch (Throwable t) {
			CrashTracker.INSTANCE.logThrowableCrash(t);
		}
	}

	//subsystems
	public static VisionBuffer getVisionSystem() {
	    return vision;
    }

	public static PowerDistributionPanel getPowerDistributionPanel() {
		return pdp;
	}

	public static Compressor getCompressor() {
		return compressor;
	}
}
