package org.team401.robot;

import com.ctre.CANTalon;
//import edu.wpi.first.wpilibj.VictorSP;
import java.util.Date;

public class Auto2017 {
	private String tgt;
	private int state = 0;
	private Date startTimeout;
	private static final int HOPPER_TIMEOUT = 1, PEG_TIMEOUT = 4;
	private double[][][] profiles;
	private boolean mecanum/*, victor*/;
	private ProfileSender fl, fr, rl, rr;
	//private VictorSP flv, frv, rlv, rrv;

	/*public Auto2017(String start, String tgt, boolean mecanum){
		this(start, tgt, mecanum, false);//Default to no victors
	}*/
	public Auto2017(String start, String tgt, boolean mecanum/*, boolean victor*/) {
		this.tgt = tgt;
		this.mecanum = mecanum;
		//this.victor = victor;
		profiles = MotionProfiles.get(start, tgt, mecanum);
		/*if (victor){//If we don't have enough Talon SRX's, we may have to manually drive 4 Victors.
			flv = new VictorSP(Constants.PRO_FRONT_LEFT);
			frv = new VictorSP(Constants.PRO_FRONT_RIGHT);
			rlv = new VictorSP(Constants.PRO_REAR_LEFT);
			rrv = new VictorSP(Constants.PRO_REAR_RIGHT);
		}*/
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
		/*if(victor){//I hate these 6 lines.  They are only in case we run out of Talon SRX's.
			flv.setSpeed(fl.getTalon().getSpeed());
			frv.setSpeed(fr.getTalon().getSpeed());
			rlv.setSpeed(rl.getTalon().getSpeed());
			rrv.setSpeed(rr.getTalon().getSpeed());
		}*/
		if(finished()) {
			state++;
			startTimeout = new Date();
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
				if(startTimeout.getTime()+(tgt.endsWith("H")?HOPPER_TIMEOUT:PEG_TIMEOUT)*1000<=new Date().getTime())
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
