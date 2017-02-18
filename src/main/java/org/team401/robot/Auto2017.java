package org.team401.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.components.OctocanumGearbox;

import java.util.List;

/**
 * To run autonomous mode, place the constructor in autonomousInit() and periodic() in autonomousPeriodic().
 * This class abstracts Autonomous code for a simpler Robot.java.
 */

public class Auto2017 {
	//Destination for first path
	private String tgt;

	//Current state in periodic()'s state machine
	private int state = 0;

	//Time at which robot pause started
	private double startTimeout;

	//Number of seconds to pause at these locations in between paths
	private static final double HOPPER_TIMEOUT = 1.0, PEG_TIMEOUT = 4.0;

	//Profiles to send to each motor
	private double[][][] profiles;

	//Are we driving with mecanum or traction wheels?
	private boolean mecanum;

	//Each sends a motion profile to its respective Talon.
	private ProfileSender fl, fr, rl, rr;

	//Reference to drive object used in Teleop
	private OctocanumDrive drive;

	/**
	 * Constructor.  Call this in autonomousInit(), please.
	 *
	 * @param start Either "L", "C", or "R".  Defines if the robot is starting in the left, center, or right side of the field.
	 * @param tgt Either "LH", "LL", "CL", "RL", or "RH".  Defines where the robot will go in the first path.
	 * @param mecanum Defines if we will be using mecanum or traction wheels.
	 * @param drive Passthrough for the Talon SRX references
	 */
	public Auto2017(String start, String tgt, boolean mecanum, OctocanumDrive drive) {
		//Save instance data
		this.tgt = tgt;
		this.mecanum = mecanum;
		this.drive = drive;

		//Tell all Talon SRXs to get ready for Motion Profile
		List<OctocanumGearbox> boxes = drive.getGearboxes();
		for(OctocanumGearbox box:boxes)
			box.getProMotor().setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Absolute);
		boxes.get(0).changeControlMode(TalonControlMode.MotionProfile);
		boxes.get(1).changeControlMode(TalonControlMode.MotionProfile);

		//Make the rear Talons followers if in Traction mode
		if(mecanum) {
			boxes.get(2).changeControlMode(TalonControlMode.MotionProfile);
			boxes.get(3).changeControlMode(TalonControlMode.MotionProfile);
		}else {
			boxes.get(2).changeControlMode(TalonControlMode.Follower);
			boxes.get(2).getProMotor().set(Constants.CIM_FRONT_LEFT);
			boxes.get(3).changeControlMode(TalonControlMode.Follower);
			boxes.get(3).getProMotor().set(Constants.CIM_FRONT_RIGHT);
		}

		//Read profiles from correct spreadsheet
		profiles = MotionProfiles.get(start, tgt, mecanum);

		//Shifts to either mecanum or traction wheels
		drive.shift(mecanum ? OctocanumDrive.DriveMode.MECANUM : OctocanumDrive.DriveMode.TRACTION);

		//Start traveling along the first profile
		startProfile();
	}

	/**
	 * Instantiates the ProfileSenders and engages them to run the current Motion Profile.
	 */
	private void startProfile(){
		//Start up the ProfileSenders

		fl = new ProfileSender(drive.getGearboxes().get(0).getProMotor(), profiles[0]);
		fr = new ProfileSender(drive.getGearboxes().get(1).getProMotor(), profiles[1]);

		//Only need 2 if in traction mode
		if(mecanum) {
			rl = new ProfileSender(drive.getGearboxes().get(2).getProMotor(), profiles[2]);
			rr = new ProfileSender(drive.getGearboxes().get(3).getProMotor(), profiles[3]);
		}

		//Start sending the profiles
		fl.startMotionProfile();
		fr.startMotionProfile();

		//Mostly, at least.
		if(mecanum) {
			rl.startMotionProfile();
			rr.startMotionProfile();
		}
	}

	/**
	 * Called every iteration if we're moving along a path.
	 * Prepares for next path if we are finished moving.
	 */
	private void move(){
		//SmartDashboard puts to make sure the code is working and tune PID
		SmartDashboard.putBoolean("Mecanum Drive", mecanum);
		SmartDashboard.putString("Actual Drive Mode", drive.getDriveMode().name());
		SmartDashboard.putString("GRAPH", ""+
			fl.getTalon().getSpeed()+":"+
			fr.getTalon().getSpeed()+":"+
			fl.getTalon().getEncVelocity()+":"+
			fr.getTalon().getEncVelocity()+
			(mecanum ?
				":"+rl.getTalon().getSpeed()+":"+
				rr.getTalon().getSpeed()+":"+
				rl.getTalon().getEncVelocity()+":"+
				rr.getTalon().getEncVelocity()
			: ""));

		//Keep the MP loops going
		fl.control();
		fr.control();
		if(mecanum) {
			rl.control();
			rr.control();
		}

		//Check if we're at the end of the path
		if(finished()) {
			//Move forward in state machine
			state++;

			//Reset the timeout so we can know how long the robot's been stopped
			startTimeout = Timer.getFPGATimestamp();

			//Reset encoders so position feed-forward doesn't get confused
			fl.reset();
			fr.reset();
			if(mecanum) {
				rl.reset();
				rr.reset();
			}

			//Get next path
			profiles = MotionProfiles.get("", tgt, mecanum, true);
		}
	}

	/**
	 * Aligns the robot with the gear peg
	 */
	private void strafe(){
		//drive.changeControlMode(TalonControlMode.Position);
	}

	/**
	 * Determines if we are finished with the current path
	 *
	 * @return True if all the ProfileSenders are finished.
	 */
	private boolean finished(){
		//The setValue is "Hold" with a value of 2 when a ProfileSender detects a finished profile.
		return fl.getSetValue().value==2&&
				fr.getSetValue().value==2&&(!mecanum||
					//Only check 2 if in traction
					rl.getSetValue().value==2&&
					rr.getSetValue().value==2);
	}

	/**
	 * State machine governing progress through Autonomous.
	 * Call this in autonomousPeriodic(), please.
	 */
	public void periodic(){
		switch(state){
			case 0:
				//Execute starting MP until finished.
				move();
				break;
			case 1:
				if(tgt.endsWith("H"))
					strafe();
				//Wait for a specified time for balls to fall in the robot or a pilot to lift the gear
				if(startTimeout+(tgt.endsWith("H") ? HOPPER_TIMEOUT : PEG_TIMEOUT) <= Timer.getFPGATimestamp())
					state++;
				break;
			case 2:
				//Start a second MP for the rest of auto
				startProfile();
				state++;
				break;
			case 3:
				//Execute second MP
				move();
				break;
			case 4:
				//auto should be done by now
				System.out.println("Autonomous finished!");
				state++;
				break;
			default:
				//do nothing if not in correct states
				break;
		}
	}
}