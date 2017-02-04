/**
 * This Java FRC robot application is meant to demonstrate an example using the Motion Profile control mode
 * in Talon SRX.  The CANTalon class gives us the ability to buffer up trajectory points and execute them
 * as the roboRIO streams them into the Talon SRX.
 * <p>
 * There are many valid ways to use this feature and this example does not sufficiently demonstrate every possible
 * method.  Motion Profile streaming can be as complex as the developer needs it to be for advanced applications,
 * or it can be used in a simple fashion for fire-and-forget actions that require precise timing.
 * <p>
 * This application is an IterativeRobot project to demonstrate a minimal implementation not requiring the command
 * framework, however these code excerpts could be moved into a command-based project.
 * <p>
 * The project also includes instrumentation.java which simply has debug printfs, and a MotionProfile.java which is generated
 * in @link https://docs.google.com/spreadsheets/d/1PgT10EeQiR92LNXEOEe3VGn737P7WDP4t0CQxQgC8k0/edit#gid=1813770630&vpid=A1
 * <p>
 * Logitech Gamepad mapping, use left y axis to drive Talon normally.
 * Press and hold top-left-shoulder-button5 to put Talon into motion profile control mode.
 * This will start sending Motion Profile to Talon while Talon is neutral.
 * <p>
 * While holding top-left-shoulder-button5, tap top-right-shoulder-button6.
 * This will signal Talon to fire MP.  When MP is done, Talon will "hold" the last setpoint position
 * and wait for another button6 press to fire again.
 * <p>
 * Release button5 to allow OpenVoltage control with left y axis.
 */

package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.Solenoid;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.chassis.OctocanumGearbox;


public class Robot extends IterativeRobot {
	private SendableChooser autoStart, autoTgt;
	private Auto2017 autonomous;
	private OctocanumDrive drive;

	private FlightStick joystick;

	@Override
	public void robotInit() {
		joystick = Hardware.HumanInterfaceDevices.logitechAttack3D(0);

		//init drive
		drive = new OctocanumDrive(
				new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT)),
				Hardware.Solenoids.doubleSolenoid(0, 1, Solenoid.Direction.EXTENDING));

		SmartDashboard.putString("", "DO NOT SELECT STARTING POSITIONS AND HOPPERS OF OPPOSITE DIRECTIONS!!!");
		//creates radio buttons for selecting the robots path
		autoStart = new SendableChooser();
		autoStart.addDefault("Center", "C");
		autoStart.addObject("Left", "L");
		autoStart.addObject("Right", "R");
		SmartDashboard.putData("Starting Position", autoStart);

		autoTgt = new SendableChooser();
		autoTgt.addDefault("Center Lift", "CL");
		autoTgt.addObject("Left Lift", "LL");
		autoTgt.addObject("Right Lift", "RL");
		autoTgt.addObject("Left Hopper", "LH");
		autoTgt.addObject("Right Hopper", "RH");


		SmartDashboard.putData("Auto Destination", autoTgt);

		SmartDashboard.putBoolean("Mecanum Drive", true);

	}

	@Override
	public void autonomousInit() {
		autonomous = new Auto2017((String)autoStart.getSelected(),
			(String)autoTgt.getSelected(),
			SmartDashboard.getBoolean("Mecanum Drive", true));
	}


	@Override
	public void autonomousPeriodic(){
		autonomous.periodic();
	}

	@Override
	public void teleopPeriodic() {
		//some driving code
	}
	@Override
	public void disabledPeriodic() {
		drive.drive(0, 0, 0, 0);
	}

}
