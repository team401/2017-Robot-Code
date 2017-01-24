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

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.Solenoid;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.chassis.OctocanumGearbox;


public class Robot extends IterativeRobot {


    CANTalon frontLeft0, frontLeft1, frontRight0, frontRight1, rearLeft0, rearLeft1, rearRight0, rearRight1;

    OctocanumDrive drive;


    /** some example logic on how one can manage an MP */
    MotionProfileExample frontLeftMP, frontRightMP, rearLeftMP, rearRightMP;

    /** cache last buttons so we can detect press events.  In a command-based project you can leverage the on-press event
     * but for this simple example, lets just do quick compares to prev-btn-states */
    boolean _btnLast = false;

    FlightStick _joy = Hardware.HumanInterfaceDevices.logitechAttack3D(0);

    //Scanner scan;

    @Override
    public void robotInit() {
        frontLeft0 = new CANTalon(0);
        frontLeft0.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
        frontLeft0.reverseSensor(false); /* keep sensor and motor in phase */
        frontLeft1 = new CANTalon(1);
        frontLeft1.changeControlMode(TalonControlMode.Follower);
        frontLeft1.set(frontLeft0.getDeviceID());

        frontRight0 = new CANTalon(2);
        frontRight0.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
        frontRight0.reverseSensor(false); /* keep sensor and motor in phase */
        frontRight1 = new CANTalon(3);
        frontRight1.changeControlMode(TalonControlMode.Follower);
        frontRight1.set(frontRight0.getDeviceID());

        rearLeft0 = new CANTalon(4);
        rearLeft0.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
        rearLeft0.reverseSensor(false); /* keep sensor and motor in phase */
        rearLeft1 = new CANTalon(5);
        rearLeft1.changeControlMode(TalonControlMode.Follower);
        rearLeft1.set(rearLeft0.getDeviceID());

        rearRight0 = new CANTalon(6);
        rearRight0.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
        rearRight0.reverseSensor(false); /* keep sensor and motor in phase */
        rearRight1 = new CANTalon(7);
        rearRight1.changeControlMode(TalonControlMode.Follower);
        rearRight1.set(rearRight0.getDeviceID());

        drive = new OctocanumDrive(
                new OctocanumGearbox(frontLeft0, frontLeft1),
                new OctocanumGearbox(frontRight0, frontRight1),
                new OctocanumGearbox(rearLeft0, rearLeft1),
                new OctocanumGearbox(rearRight0, rearRight1),
                Hardware.Solenoids.doubleSolenoid(0, 1, Solenoid.Direction.EXTENDING));

    }

    /*@Override
    public void autonomousInit() {//For when we actually motion profile
        leftMotor.changeControlMode(TalonControlMode.MotionProfile);
        rightMotor.changeControlMode(TalonControlMode.MotionProfile);
        leftMP.startMotionProfile();
        rightMP.startMotionProfile();
    }

    @Override
    public void autonomousPeriodic() {

        leftMP.control();
        rightMP.control();
    }*/
    @Override
    public void autonomousInit(){//For now, just calculate the F-gain.

    }

    @Override
    public void autonomousPeriodic() {
        double driveSpeed = _joy.getPitch().read();
        double turnSpeed = _joy.getRoll().read();

		/* get the left joystick axis on Logitech Gamepad */
        double leftYjoystick = -1 * _joy.getPitch().read(); /* multiple by -1 so joystick forward is positive */

		/* call this periodically, and catch the output.  Only apply it if user wants to run MP. */
        //_example.control();


        if (!_joy.getThumb().isTriggered()) { /* Check button 5 (top left shoulder on the logitech gamead). */
            /*
			 * If it's not being pressed, just do a simple drive.  This
			 * could be a RobotDrive class or custom drivetrain logic.
			 * The point is we want the switch in and out of MP Control mode.*/

			/* button5 is off so straight drive */

            drive.arcade(driveSpeed, turnSpeed);

            //_example.reset();
        } else {
			/* Button5 is held down so switch to motion profile control mode => This is done in MotionProfileControl.
			 * When we transition from no-press to press,
			 * pass a "true" once to MotionProfileControl.
			 */
            //_talon.changeControlMode(TalonControlMode.MotionProfile);
            leftMotor.changeControlMode(TalonControlMode.MotionProfile);
            rightMotor.changeControlMode(TalonControlMode.MotionProfile);


            CANTalon.SetValueMotionProfile setOutput = null;//_example.getSetValue();

            //_talon.set(setOutput.value);
            leftMotor.set(setOutput.value);
            rightMotor.set(setOutput.value);

			/* if btn is pressed and was not pressed last time,
			 * In other words we just detected the on-press event.
			 * This will signal the robot to start a MP */
            if ((_joy.getTrigger().isTriggered()) && (_btnLast == false)) {
				/* user just tapped the trigger  */
                //_example.startMotionProfile();
            }
        }

        _btnLast = _joy.getTrigger().isTriggered();
    }

    /**  function is called periodically during operator control */
    @Override
    public void teleopPeriodic() {
        double speed = _joy.getTrigger().isTriggered() ? 1.0 : 0.0;
        drive.drive(speed, speed, speed, speed);
        SmartDashboard.putNumber("FLVEL", frontLeft0.getEncVelocity());
        SmartDashboard.putNumber("FRVEL", frontRight0.getEncVelocity());
        SmartDashboard.putNumber("RLVEL", rearLeft0.getEncVelocity());
        SmartDashboard.putNumber("RRVEL", rearRight0.getEncVelocity());
        SmartDashboard.putNumber("FLPOS", frontLeft0.getEncPosition());
        SmartDashboard.putNumber("FRPOS", frontRight0.getEncPosition());
        SmartDashboard.putNumber("RLPOS", rearLeft0.getEncPosition());
        SmartDashboard.putNumber("RRPOS", rearRight0.getEncPosition());
    }
    @Override
    public void disabledPeriodic() {
		drive.drive(0,0,0,0);
    }
}
