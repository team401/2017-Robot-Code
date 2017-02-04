package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;

/**
 * To run autonomous mode, place the constructor in autonomousInit() and periodic() in autonomousPeriodic().
 * This class will handle
 */

public class Auto2017 {
	private String tgt;
	private int state = 0;
	private double startTimeout;
	private static final double HOPPER_TIMEOUT = 1.0, PEG_TIMEOUT = 4.0;//Number of seconds to pause at these locations
	private double[][][] profiles;
	private boolean mecanum;
	private ProfileSender fl, fr, rl, rr;

	public Auto2017(String start, String tgt, boolean mecanum) {
		this.tgt = tgt;
		this.mecanum = mecanum;
		profiles = MotionProfiles.get(start, tgt, mecanum);
		startProfile();
	}

	private void startProfile(){
		fl = new ProfileSender(new CANTalon(Constants.CIM_FRONT_LEFT), profiles[0]);
		//These 3 lines should be copied to all the secondary Talons if the rest of the robot code doesn't correctly establish followers.
		/*CANTalon flt = new CANTalon(Constants.PRO_FRONT_LEFT);
		flt.changeControlMode(CANTalon.TalonControlMode.Follower);
		flt.set(Constants.CIM_FRONT_LEFT);*/
		fr = new ProfileSender(new CANTalon(Constants.CIM_FRONT_RIGHT), profiles[1]);
		rl = new ProfileSender(new CANTalon(Constants.CIM_REAR_LEFT), profiles[mecanum ? 2 : 0]);
		rr = new ProfileSender(new CANTalon(Constants.CIM_REAR_RIGHT), profiles[mecanum ? 3 : 1]);
		fl.startMotionProfile();
		fr.startMotionProfile();
		rl.startMotionProfile();
		rr.startMotionProfile();
	}
	private void move(){
		//Keeps the MP loops going and increments state if at the end of the profile
		fl.control();
		fr.control();
		rl.control();
		rr.control();
		if(finished()) {
			state++;
			startTimeout = Timer.getFPGATimestamp();
			//Reset encoders so position feed-forward doesn't get confused
			fl.resetEncoder();
			fr.resetEncoder();
			rl.resetEncoder();
			rr.resetEncoder();
			profiles = MotionProfiles.get("", tgt, mecanum, true);
		}
	}
	private boolean finished(){//Should return true if and only if a profile is finished.
		return fl.getSetValue().value==2&&
				fr.getSetValue().value==2&&
				rl.getSetValue().value==2&&
				rr.getSetValue().value==2;
	}

	public void periodic(){
		switch(state){
			case 0://Execute starting MP until finished
				move();
				break;
			case 1://Wait for a specified time for balls to fall in the robot or a pilot to lift the gear
				if(startTimeout+(tgt.endsWith("H") ? HOPPER_TIMEOUT : PEG_TIMEOUT)<=Timer.getFPGATimestamp())
					state++;
				break;
			case 2://Start a second MP for the rest of auto
				startProfile();
				break;
			case 3:
				move();
				break;
			case 4:
				//auto should be done by now
				System.out.println("Autonomous finished!");
				break;
			default:
				//do nothing if not in correct states
				break;
		}
	}
}
