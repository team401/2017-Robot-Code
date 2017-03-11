package org.team401.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotTestPls /*extends IterativeRobot*/ {

	private Servo servo;

	//@Override
	public void robotInit() {
		SmartDashboard.putNumber("servo_setpoint", 0.0);
		servo = new Servo(0);
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
		servo.set(SmartDashboard.getNumber("servo_setpoint", 0.0));
	}
}
