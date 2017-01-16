/**
 * This Java FRC robot application is meant to demonstrate an example using the Motion Profile control mode
 * in Talon SRX.  The CANTalon class gives us the ability to buffer up trajectory points and execute them
 * as the roboRIO streams them into the Talon SRX.
 * 
 * There are many valid ways to use this feature and this example does not sufficiently demonstrate every possible
 * method.  Motion Profile streaming can be as complex as the developer needs it to be for advanced applications,
 * or it can be used in a simple fashion for fire-and-forget actions that require precise timing.
 * 
 * This application is an IterativeRobot project to demonstrate a minimal implementation not requiring the command 
 * framework, however these code excerpts could be moved into a command-based project.
 * 
 * The project also includes instrumentation.java which simply has debug printfs, and a MotionProfile.java which is generated
 * in @link https://docs.google.com/spreadsheets/d/1PgT10EeQiR92LNXEOEe3VGn737P7WDP4t0CQxQgC8k0/edit#gid=1813770630&vpid=A1
 * 
 * Logitech Gamepad mapping, use left y axis to drive Talon normally.  
 * Press and hold top-left-shoulder-button5 to put Talon into motion profile control mode.
 * This will start sending Motion Profile to Talon while Talon is neutral. 
 * 
 * While holding top-left-shoulder-button5, tap top-right-shoulder-button6.
 * This will signal Talon to fire MP.  When MP is done, Talon will "hold" the last setpoint position
 * and wait for another button6 press to fire again.
 * 
 * Release button5 to allow OpenVoltage control with left y axis.
 */

package org.team401.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;

import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.TalonSRX;
import org.strongback.components.ui.FlightStick;
import org.strongback.drive.TankDrive;
import org.strongback.hardware.Hardware;


public class Robot extends IterativeRobot {





	CANTalon leftMotor;
	CANTalon rightMotor;

	TankDrive drive;


	/** The Talon we want to motion profile. */
	CANTalon _talon = new CANTalon(9);

	/** some example logic on how one can manage an MP */
	MotionProfileExample _example = new MotionProfileExample(_talon);

	/** cache last buttons so we can detect press events.  In a command-based project you can leverage the on-press event
	 * but for this simple example, lets just do quick compares to prev-btn-states */
	boolean _btnLast = false;

	FlightStick _joy = Hardware.HumanInterfaceDevices.logitechAttack3D(0);

	FalconPathPlanner falcon;
	double[][] waypoints = new double[][]{
			{1, 1},
			{2, 2},
			{3, 3}
	};

	public void robotInit(){
		falcon = new FalconPathPlanner(waypoints);
		falcon.calculate(60, 0.02, 14.5);

		leftMotor = new CANTalon(2);
		leftMotor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		leftMotor.reverseSensor(false); /* keep sensor and motor in phase */

		rightMotor = new CANTalon(6);
		rightMotor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		rightMotor.reverseSensor(false); /* keep sensor and motor in phase */

		CANTalon[] followers = new CANTalon[]{
				new CANTalon(0),//middle left
				new CANTalon(1),//Rear left
				//new CANTalon(2),//Front left
				//new CANTalon(3),//Left Shooter
				//new CANTalon(4),//Dart
				new CANTalon(5),//Rear right
				//new CANTalon(6),//front right
				new CANTalon(7),//middle right
				//new CANTalon(8),//right shooter


		};

		for(CANTalon u:followers)
			u.changeControlMode(TalonControlMode.Follower);
		followers[0].set(2);
		followers[1].set(2);
		followers[2].set(6);
		followers[3].set(6);
		followers[0].reverseOutput(true);
		followers[3].reverseOutput(true);

		drive = new TankDrive(Hardware.Motors.talonSRX(leftMotor),
				Hardware.Motors.talonSRX(rightMotor));



		//_talon.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
		//_talon.reverseSensor(false); /* keep sensor and motor in phase */
	}
	private int i;
	public void autonomousInit(){
		i = 0;
	}
	public void autonomousPeriodic(){

		i++;
		drive.arcade(falcon.getYVector(falcon.smoothCenterVelocity)[i], falcon.getXVector(falcon.smoothCenterVelocity)[i]);

	}

	/**  function is called periodically during operator control */
    public void teleopPeriodic() {
double driveSpeed = _joy.getPitch().read();
double turnSpeed = _joy.getRoll().read();

		/* get the left joystick axis on Logitech Gampead */
		double leftYjoystick = -1 * _joy.getPitch().read(); /* multiple by -1 so joystick forward is positive */

		/* call this periodically, and catch the output.  Only apply it if user wants to run MP. */
		_example.control();


		if (!_joy.getThumb().isTriggered()) { /* Check button 5 (top left shoulder on the logitech gamead). */
			/*
			 * If it's not being pressed, just do a simple drive.  This
			 * could be a RobotDrive class or custom drivetrain logic.
			 * The point is we want the switch in and out of MP Control mode.*/

			/* button5 is off so straight drive */

			drive.arcade(driveSpeed, turnSpeed);

			_example.reset();
		} else {
			/* Button5 is held down so switch to motion profile control mode => This is done in MotionProfileControl.
			 * When we transition from no-press to press,
			 * pass a "true" once to MotionProfileControl.
			 */
			//_talon.changeControlMode(TalonControlMode.MotionProfile);
			leftMotor.changeControlMode(TalonControlMode.MotionProfile);
			rightMotor.changeControlMode(TalonControlMode.MotionProfile);


			CANTalon.SetValueMotionProfile setOutput = _example.getSetValue();

			//_talon.set(setOutput.value);
			leftMotor.set(setOutput.value);
			rightMotor.set(setOutput.value);

			/* if btn is pressed and was not pressed last time,
			 * In other words we just detected the on-press event.
			 * This will signal the robot to start a MP */
			if( (_joy.getTrigger().isTriggered()) && (_btnLast == false) ) {
				/* user just tapped the trigger  */
				_example.startMotionProfile();
			}
		}

		_btnLast = _joy.getTrigger().isTriggered();

	}
	/**  function is called periodically during disable */
	public void disabledPeriodic() {
		/* it's generally a good idea to put motor controllers back
		 * into a known state when robot is disabled.  That way when you
		 * enable the robot doesn't just continue doing what it was doing before.
		 * BUT if that's what the application/testing requires than modify this accordingly */
		//_talon.changeControlMode(TalonControlMode.PercentVbus);
		//_talon.set(0);
		leftMotor.set(0);
		rightMotor.set(0);
		/* clear our buffer and put everything into a known state */
		_example.reset();
	}
}
