package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.lib.LoopManager;
import org.team401.robot.subsystems.Flywheel;
import org.team401.robot.subsystems.Hopper;
import org.team401.robot.subsystems.Tower;

public class RobotTestPls /*extends IterativeRobot*/ {

	private LoopManager loop = new LoopManager();
	private CANTalon kicker;

	public void robotInit() {
		SmartDashboard.putNumber("flywheel_user_setpoint", 0.0);

		loop.register(Flywheel.INSTANCE.getSubsystemLoop());
		loop.register(Hopper.INSTANCE.getSubsystemLoop());
		loop.register(Tower.INSTANCE.getSubsystemLoop());
		Tower.INSTANCE.setWantedState(Tower.TowerState.TOWER_OUT);

		kicker = new CANTalon(Constants.TURRET_FEEDER);
		kicker.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		kicker.enableBrakeMode(true);
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
			Flywheel.INSTANCE.setSpeed(delta);
			kicker.set(1);
		} else {
			Flywheel.INSTANCE.stop();
			kicker.set(0);
		}

		Flywheel.INSTANCE.printToSmartDashboard();
		Hopper.INSTANCE.printToSmartDashboard();
	}
}
