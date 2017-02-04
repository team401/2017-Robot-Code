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
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;

import org.strongback.Strongback;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.Motor;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.TankDrive;
import org.strongback.hardware.Hardware;
import org.team401.robot.chassis.Hopper;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.chassis.OctocanumGearbox;
import org.team401.robot.commands.ToggleDriveMode;


public class Robot extends IterativeRobot {
	private SendableChooser autoStart,
		autoTgt;
	CANTalon leftMotor;
	CANTalon rightMotor;

	TankDrive drive;
	CANTalon.TrajectoryPoint point;
	CANTalon.MotionProfileStatus status;


	/** The Talon we want to motion profile. */
	//CANTalon _talon = new CANTalon(9);

	/** some example logic on how one can manage an MP */
	MotionProfileExample leftMP, rightMP, frontLeftMP, frontRightMP, rearLeftMP, rearRightMP;//_example = new MotionProfileExample(_talon);

	/** cache last buttons so we can detect press events.  In a command-based project you can leverage the on-press event
	 * but for this simple example, lets just do quick compares to prev-btn-states */
	boolean _btnLast = false;

	FlightStick _joy;

	@Override
	public void robotInit() {
		_joy = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
		leftMotor = new CANTalon(0);
		leftMotor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		leftMotor.reverseSensor(false); /* keep sensor and motor in phase */

		rightMotor = new CANTalon(1);
		rightMotor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		rightMotor.reverseSensor(false); /* keep sensor and motor in phase */

		drive = new TankDrive(Hardware.Motors.talonSRX(leftMotor),
				Hardware.Motors.talonSRX(rightMotor));

		leftMP = new MotionProfileExample(leftMotor);
		rightMP = new MotionProfileExample(rightMotor);

		point = new CANTalon.TrajectoryPoint();
		status = new CANTalon.MotionProfileStatus();

//creates drop-down menu's for selecting the robots path
		autoStart = new SendableChooser();
		autoStart.addDefault("Middle", "M");
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
	private Auto2017 autonomous;
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
	public void teleopInit(){
		leftMotor.changeControlMode(TalonControlMode.PercentVbus);
		rightMotor.changeControlMode(TalonControlMode.PercentVbus);
	}

	/**  function is called periodically during operator control */
	@Override
	public void teleopPeriodic() {
		double driveSpeed = _joy.getPitch().read();
		double turnSpeed = _joy.getRoll().read();


		if (!_joy.getThumb().isTriggered()) {
			drive.arcade(driveSpeed, turnSpeed);
		} else {
			leftMotor.changeControlMode(TalonControlMode.MotionProfile);
			rightMotor.changeControlMode(TalonControlMode.MotionProfile);


			CANTalon.SetValueMotionProfile setOutput = null;//_example.getSetValue();
			leftMotor.set(setOutput.value);
			rightMotor.set(setOutput.value);

			if ((_joy.getTrigger().isTriggered()) && (_btnLast == false)) {
			}
		}

		_btnLast = _joy.getTrigger().isTriggered();

	}
	@Override
	public void disabledPeriodic() {
		leftMotor.set(0);
		rightMotor.set(0);
	}

}
