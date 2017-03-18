package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.robot.loops.LoopManager;
import org.team401.robot.subsystems.Flywheel;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class RobotTestPls /*extends IterativeRobot*/ {

	private Servo left, right;
	private Solenoid gear;

	//@Override
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
		if (ControlBoard.INSTANCE.getShift().isTriggered())
			gear.set(true);
		else
			gear.set(false);
		left.setAngle(SmartDashboard.getNumber("servo_left_setpoint", 0.0));
		right.setAngle(SmartDashboard.getNumber("servo_right_setpoint", 0.0));
	}
}
