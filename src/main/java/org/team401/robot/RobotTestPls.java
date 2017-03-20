package org.team401.robot;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTestPls /*extends IterativeRobot*/ {

	private Servo left, right;
	private Solenoid gear;

	public void robotInit() {
		left = new Servo(Constants.SERVO_LEFT);
		right = new Servo(Constants.SERVO_RIGHT);
		gear = new Solenoid(Constants.GEAR_HOLDER);

		SmartDashboard.putNumber("servo_left_setpoint", 0.0);
		SmartDashboard.putNumber("servo_right_setpoint", 0.0);

		/*kicker = new CANTalon(Constants.TURRET_FEEDER);
		kicker.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		kicker.enableBrakeMode(true);*/
	}

	public void autonomousInit() {

	}

	public void teleopInit() {

	}

	public void disabledInit() {

	}

	public void autonomousPeriodic() {

	}

	public void teleopPeriodic() {
		if (ControlBoard.INSTANCE.getShift().isTriggered())
			gear.set(true);
		else
			gear.set(false);
		left.setAngle(SmartDashboard.getNumber("servo_left_setpoint", 0.0));
		right.setAngle(SmartDashboard.getNumber("servo_right_setpoint", 0.0));
	}
}
