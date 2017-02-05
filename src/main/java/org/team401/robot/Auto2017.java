package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.components.OctocanumGearbox;

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
		for (OctocanumGearbox x:drive.getGearboxes()) {
			x.setControlMode(CANTalon.TalonControlMode.MotionProfile);
		}

		//Read profiles from correct spreadsheet
		profiles = MotionProfiles.get(start, tgt, mecanum);

		//Shifts to either mecanum or traction wheels
		drive.shift(mecanum ? OctocanumDrive.DriveMode.MECHANUM : OctocanumDrive.DriveMode.TRACTION);

		//Start traveling along the first profile
		startProfile();
	}

	/**
	 * Instantiates the ProfileSenders and engages them to run the current Motion Profile.
	 */
	private void startProfile(){
		//Start up the ProfileSenders
		fl = new ProfileSender(drive.getGearboxes().get(0).getCimMotor(), profiles[0]);
		fr = new ProfileSender(drive.getGearboxes().get(1).getCimMotor(), profiles[1]);
		if(mecanum) {
			rl = new ProfileSender(drive.getGearboxes().get(2).getCimMotor(), profiles[2]);
			rr = new ProfileSender(drive.getGearboxes().get(3).getCimMotor(), profiles[3]);
		}else{
			//If we are in tank drive, make all MPs run on 1 sensor for each side.
			drive.getGearboxes().get(2).setControlMode(CANTalon.TalonControlMode.Follower);
			drive.getGearboxes().get(2).getCimMotor().set(Constants.CIM_FRONT_LEFT);
			drive.getGearboxes().get(3).setControlMode(CANTalon.TalonControlMode.Follower);
			drive.getGearboxes().get(3).getCimMotor().set(Constants.CIM_FRONT_RIGHT);
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
		//Keeps the MP loops going and increments state if at the end of the profile
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
			fl.resetEncoder();
			fr.resetEncoder();
			if(mecanum) {
				rl.resetEncoder();
				rr.resetEncoder();
			}

			//Get next path
			profiles = MotionProfiles.get("", tgt, mecanum, true);
		}
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
				//Wait for a specified time for balls to fall in the robot or a pilot to lift the gear
				if(startTimeout+(tgt.endsWith("H") ? HOPPER_TIMEOUT : PEG_TIMEOUT)<=Timer.getFPGATimestamp())
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