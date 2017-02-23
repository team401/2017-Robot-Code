package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.ui.FlightStick;

public class RobotTestPls /*extends IterativeRobot*/ {

	private CANTalon flywheel, slave;

	private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;

	//@Override
	public void robotInit() {
		flywheel = new CANTalon(Constants.TURRET_SHOOTER_SLAVE);
		flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
		flywheel.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		flywheel.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
				Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0);
		slave = new CANTalon(Constants.TURRET_SHOOTER_MASTER);
		slave.changeControlMode(CANTalon.TalonControlMode.Follower);
		slave.set(Constants.TURRET_SHOOTER_MASTER);
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
		flywheel.set(SmartDashboard.getNumber("flywheel_setpoint", 0));
		SmartDashboard.putNumber("flywheel_velocity", flywheel.getSpeed());
		SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());
	}
}
