package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.ui.FlightStick;

public class RobotTestPls /*extends IterativeRobot*/ {

	private CANTalon flywheel, feeder;

	private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;

	//@Override
	public void robotInit() {
		SmartDashboard.putNumber("flywheel_setpoint", 0.0);

		feeder = new CANTalon(Constants.TURRET_FEEDER);

		flywheel = new CANTalon(Constants.TURRET_SHOOTER_MASTER);
		flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
		flywheel.reverseSensor(true);
		flywheel.reverseOutput(true);
		flywheel.configPeakOutputVoltage(0, -12);
		flywheel.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		flywheel.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
				Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0);

		CANTalon slave = new CANTalon(Constants.TURRET_SHOOTER_SLAVE);
		slave.setInverted(true);
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
		if (SmartDashboard.getNumber("flywheel_setpoint", 0) > 0)
			feeder.set(1);
		else
			feeder.set(0);
		SmartDashboard.putNumber("flywheel_velocity", (int) flywheel.getSpeed());
		SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());
	}
}
