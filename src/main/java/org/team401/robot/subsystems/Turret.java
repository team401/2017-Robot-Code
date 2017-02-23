package org.team401.robot.subsystems;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.Switch;
import org.strongback.components.ui.ContinuousRange;
import org.team401.robot.Constants;
import org.team401.robot.Robot;
import org.team401.robot.components.TurretRotator;
import org.team401.robot.loops.Loop;
import org.team401.robot.math.MathUtils;
import org.team401.robot.sensors.DistanceSensor;
import org.team401.vision.VisionDataStream.VisionData;

public class Turret extends Subsystem {

	public enum TurretState {
		DISABLED, CALIBRATING, MANUAL, SENTRY, AUTO
	}

	private TurretState state = TurretState.DISABLED;

	private TurretRotator turretRotator;
	private Solenoid turretHood, ledRing;

	private VisionData latestData;
	private DistanceSensor distanceSensor;
	private Switch trigger;

	private CANTalon flywheel, feeder;

	private ContinuousRange yaw, throttle;

	private Loop loop = new Loop() {
		@Override
		public void onStart() {
			setWantedState(TurretState.SENTRY);
		}

		@Override
		public void onLoop() {
			run();
			printToSmartDashboard();
		}

		@Override
		public void onStop() {
			setWantedState(TurretState.MANUAL);
			turretRotator.stop();
		}
	};

	public Turret(DistanceSensor distanceSensor, CANTalon turretSpinner, CANTalon flyWheelMaster,
				  CANTalon flywheelSlave, CANTalon turretFeeder, Solenoid turretHood, Solenoid ledRing,
				  Switch trigger, ContinuousRange yaw, ContinuousRange throttle) {
		turretRotator = new TurretRotator(turretSpinner);
		latestData = new VisionData(0, 0, 0);
		this.trigger = trigger;
		this.turretHood = turretHood;
		this.ledRing = ledRing;
		this.yaw = yaw;
		this.throttle = throttle;
		this.distanceSensor = distanceSensor;

		turretHood.set(false);
		ledRing.set(false);

		flywheelSlave.setSafetyEnabled(false);
		flywheelSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
		flywheelSlave.set(flyWheelMaster.getDeviceID());
		flywheelSlave.setInverted(true);
		flywheel = flyWheelMaster;
		flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
		flywheel.configPeakOutputVoltage(12, 0);
		flywheel.setSafetyEnabled(false);
		flywheel.set(0);
		flywheel.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
				Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0);

		feeder = turretFeeder;
		feeder.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		feeder.enableLimitSwitch(true, false);
		feeder.set(0.0);
	}

	/**
	 * @return true if were looking directly at the turret
	 */
	private boolean track() {
		if (Math.abs(latestData.getYaw()) < 2) {
			turretRotator.stop();
			return true;
		}
		turretRotator.addDegrees(-latestData.getYaw()*.75);
		return false;
	}

	private double getSpeedForDistance() {
		double distance = distanceSensor.getDistance();
		return 0.0;
	}

	private void sentryMode() {
		if (turretRotator.getPosition() >= turretRotator.getMaxAngle())
			turretRotator.setPosition(-1);
		else if (turretRotator.getPosition() <= 0)
			turretRotator.setPosition(turretRotator.getMaxAngle() + 1);
	}

	private void run() {
		latestData = Robot.getVisionDataStream().getLatestGoalData();
		double speed = 0.0;
		// rotation code
		if (state.compareTo(TurretState.SENTRY) >= 0) { // auto turret control
			if (latestData.isValid()) {
				if (track())
					speed = getSpeedForDistance();
			} else {
				sentryMode();
			}
		} else if (state == TurretState.MANUAL) { // manual turret control
			double turnSpeed = yaw.read();
			if (Math.abs(turnSpeed) > .1)
				if (turnSpeed > 0)
					turretRotator
							.rotate(MathUtils.INSTANCE.toRange(turnSpeed, .1, 1, .05, .15));
				else
					turretRotator
							.rotate(-MathUtils.INSTANCE.toRange(-turnSpeed, .1, 1, .1, .2));
			speed = MathUtils.INSTANCE.toRange(throttle.read() * -1, -1, 1, 1000, 4500);
		}
		// shooting code
		if (state == TurretState.AUTO && speed != 0) { // auto shooting
			flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
			flywheel.set(speed);
			if (GearHolder.INSTANCE.getCurrentState() != GearHolder.GearHolderState.TOWER_IN)
				feeder.set(.50);
		} else if ((state == TurretState.SENTRY || state == TurretState.MANUAL) && trigger.isTriggered()) { // manual shooting
			flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
			flywheel.set(speed);
			if (GearHolder.INSTANCE.getCurrentState() != GearHolder.GearHolderState.TOWER_IN)
				feeder.set(.50);
		} else { // dont shoot
			flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
			flywheel.set(0);
			feeder.set(0);
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void zeroSensors() {
		turretRotator.stop();
		turretRotator.zero();
	}

	public void setWantedState(TurretState state) {
		this.state = state;
	}

	public TurretState getCurrentState() {
		return state;
	}

	public void extendHood(boolean extended) {
		turretHood.set(extended);
	}

	public boolean isHoodExtended() {
		return turretHood.get();
	}

	public TurretRotator getTurretRotator() {
		return turretRotator;
	}

	public boolean atZeroPoint() {
		return feeder.isFwdLimitSwitchClosed();
	}

	@Override
	public void printToSmartDashboard() {
		SmartDashboard.putNumber("flywheel_velocity", flywheel.getSpeed());
		SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());
		SmartDashboard.putNumber("turret_position", turretRotator.getPosition());
		SmartDashboard.putNumber("turret_error", turretRotator.getError());
		SmartDashboard.putNumber("goal_distance", distanceSensor.getDistance());
		SmartDashboard.putBoolean("valid_vision_data", latestData.isValid());
		SmartDashboard.putBoolean("turret_on_target", turretRotator.onTarget());
		SmartDashboard.putBoolean("turret_hood_extended", turretHood.get());
		SmartDashboard.putBoolean("limit_switch_triggered", atZeroPoint());
		SmartDashboard.putBoolean("sentry_enabled", state.compareTo(TurretState.SENTRY) >= 0);
		SmartDashboard.putBoolean("auto_shooting_enabled", state == TurretState.AUTO);
	}

	public Loop getSubsystemLoop() {
		return loop;
	}
}

