package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTestPls /*extends IterativeRobot*/ {

	private CANTalon flywheel, slave, kicker;

	//@Override
	public void robotInit() {
		SmartDashboard.putNumber("flywheel_setpoint", 0.0);
		flywheel = new CANTalon(Constants.TURRET_FLYWHEEL_MASTER);
		slave = new CANTalon(Constants.TURRET_FLYWHEEL_SLAVE);
		slave.setInverted(true);
		slave.changeControlMode(CANTalon.TalonControlMode.Follower);
		slave.set(Constants.TURRET_FLYWHEEL_MASTER);

		flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		flywheel.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		flywheel.configPeakOutputVoltage(12, 0);
		flywheel.reverseSensor(true);
		flywheel.set(0);
		flywheel.setSafetyEnabled(false);
		flywheel.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
				Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0);

		kicker = new CANTalon(Constants.TURRET_FEEDER);
		kicker.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
	}

	//@Override
	public void autonomousInit() {

	}

	//@Override
	public void teleopInit() {

	}

	//@Override
	public void disabledInit() {

	}

	//@Override
	public void autonomousPeriodic() {

	}

	//@Override
	public void teleopPeriodic() {
		double delta = SmartDashboard.getNumber("flywheel_setpoint", 0.0);
		if (delta > 0) {
			flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
			flywheel.set(delta);
			kicker.set(1);
		} else {
			flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
			flywheel.set(0);
			kicker.set(0);
		}
		SmartDashboard.putNumber("flywheel_rpm", flywheel.getSpeed());
		SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());
	}
}
