package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.lib.LoopManager;
import org.team401.robot.subsystems.Flywheel;
import org.team401.robot.subsystems.Hopper;
import org.team401.robot.subsystems.Intake;
import org.team401.robot.subsystems.Tower;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class RobotTestPls extends IterativeRobot {

	private LoopManager loop = new LoopManager();
	private CANTalon kicker;
	private Solenoid hood;

	private VisionDataStream visionDataStream;

	public void robotInit() {
		SmartDashboard.putNumber("flywheel_user_setpoint", 0.0);

		visionDataStream = new VisionDataStream("10.4.1.17", 5801);
		visionDataStream.start();

		loop.register(Flywheel.INSTANCE.getSubsystemLoop());
		//loop.register(Hopper.INSTANCE.getSubsystemLoop());
		//loop.register(Intake.INSTANCE.getSubsystemLoop());
		loop.register(Tower.INSTANCE.getSubsystemLoop());
		loop.start();
		Tower.INSTANCE.setWantedState(Tower.TowerState.TOWER_OUT);

		kicker = new CANTalon(Constants.TURRET_FEEDER);
		kicker.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		kicker.enableBrakeMode(true);

		hood = new Solenoid(Constants.TURRET_HOOD);
		hood.set(true);
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
		double delta = SmartDashboard.getNumber("flywheel_user_setpoint", 0.0);
		if (delta > 0) {
			Flywheel.INSTANCE.setSpeed((int)delta);
			//Hopper.INSTANCE.setWantedState(Hopper.HopperState.ON);
			kicker.set(1);
		} else {
			Flywheel.INSTANCE.stop();
			//Hopper.INSTANCE.setWantedState(Hopper.HopperState.OFF);
			kicker.set(0);
		}

		SmartDashboard.putNumber("vision_distance", visionDataStream.getLatestGoalDistance());
		Flywheel.INSTANCE.printToSmartDashboard();
		//Hopper.INSTANCE.printToSmartDashboard();
	}
}
