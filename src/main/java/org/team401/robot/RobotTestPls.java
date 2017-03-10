package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.CalibrateTurretMode;
import org.team401.robot.loops.LoopManager;
import org.team401.robot.subsystems.Turret;

public class RobotTestPls /*extends IterativeRobot*/ {

	private Turret turret;
	private AutoModeExecuter autoExecuter;
	private LoopManager loop;

	//@Override
	public void robotInit() {
		SmartDashboard.putNumber("flywheel_setpoint", 0.0);

		turret = Turret.getInstance();
		loop = new LoopManager();
		loop.register(turret.getSubsystemLoop());

	}

	//@Override
	public void autonomousInit() {
		autoExecuter = new AutoModeExecuter(new CalibrateTurretMode());
		autoExecuter.start();
		loop.start();
	}

	//@Override
	public void teleopInit() {
	}

	//@Override
	public void disabledInit() {
		loop.stop();
	}

	//@Override
	public void autonomousPeriodic() {

	}

	//@Override
	public void teleopPeriodic() {
		/*flywheel.set(SmartDashboard.getNumber("flywheel_setpoint", 0));
		if (SmartDashboard.getNumber("flywheel_setpoint", 0) > 0)
			feeder.set(1);
		else
			feeder.set(0);
		SmartDashboard.putNumber("flywheel_velocity", (int) flywheel.getSpeed());
		SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());*/
	}
}
