package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.robot.loops.LoopManager;
import org.team401.robot.subsystems.Flywheel;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class RobotTestPls extends IterativeRobot {

	private CANTalon kicker;
	private Solenoid gear;
	private static VisionDataStream visionDataStream;

	private LoopManager loop;

	//@Override
	public void robotInit() {
		visionDataStream = new VisionDataStream("10.4.1.17", 5801);
		visionDataStream.start();

		gear = new Solenoid(Constants.GEAR_HOLDER);

		SmartDashboard.putNumber("flywheel_user_setpoint", 0.0);

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
		double delta = SmartDashboard.getNumber("flywheel_user_setpoint", 0.0);
		if (delta > 0) {
			Flywheel.INSTANCE.setSpeed(delta);
			//kicker.set(1);
		} else {
			Flywheel.INSTANCE.stop();
			//kicker.set(0);
		}
		if (ControlBoard.INSTANCE.getShift().isTriggered())
			gear.set(true);
		else
			gear.set(false);
		Flywheel.INSTANCE.printToSmartDashboard();
		SmartDashboard.putNumber("vision_distance", visionDataStream.getLatestGoalDistance());
		SmartDashboard.putNumber("vision_yaw", visionDataStream.getLatestGearYaw());
		SmartDashboard.putBoolean("valid_vision_data", visionDataStream.isLatestGoalValid());
	}
}
