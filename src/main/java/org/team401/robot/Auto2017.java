package org.team401.robot;

import com.ctre.CANTalon;
import org.team401.robot.chassis.OctocanumDrive;

/**
 * Created by Driver Station on 2/3/2017.
 */
public class Auto2017 {
	private String tgt;
	private int state;
	private final int HOPPER_TIMEOUT = 1, PEG_TIMEOUT = 4;
	private double[][][] profiles;
	private boolean mecanum;
	private SRXProfile fl, fr, rl, rr;

	public Auto2017(String start, String tgt, boolean mecanum){
		this.tgt = tgt;
		this.mecanum = mecanum;
		state = mecanum ? 0 : 1;
		profiles = GeneratedMotionProfiles.getProfile(start, tgt, mecanum);
		startProfile();
	}
	private int i = 0;
	private void startProfile(){
		fl = new SRXProfile(new CANTalon(Constants.CIM_FRONT_LEFT), profiles[0]);
		fr = new SRXProfile(new CANTalon(Constants.CIM_FRONT_RIGHT), profiles[1]);
		rl = new SRXProfile(new CANTalon(Constants.CIM_REAR_LEFT), profiles[mecanum ? 2 : 0]);
		rr = new SRXProfile(new CANTalon(Constants.CIM_REAR_RIGHT), profiles[mecanum ? 3 : 1]);
		fl.startMotionProfile();
		fr.startMotionProfile();
		rl.startMotionProfile();
		rr.startMotionProfile();
	}
	private void control(){
		fl.control();
		fr.control();
		rl.control();
		rr.control();
	}
	private boolean finished(){
		return fl.getSetValue().value==2&&
				fr.getSetValue().value==2&&
				rl.getSetValue().value==2&&
				rr.getSetValue().value==2;
	}
	public void periodic(){
		switch(state){
			case 0:
				control();
				if(finished())
					state++;
				break;
			case 1:
				i++;
				if(i>=(tgt.endsWith("H")?HOPPER_TIMEOUT:PEG_TIMEOUT)*50)
					state++;
				break;
			case 2:
				fl.resetEncoder();
				fr.resetEncoder();
				rl.resetEncoder();
				rr.resetEncoder();
				profiles = GeneratedMotionProfiles.getProfile("", tgt, mecanum);
				startProfile();
				break;
			case 3:
				control();
				if(finished())
					state++;
				break;
			case 4:
				//auto should be done by now
				break;
			default:
				//do nothing if not in correct states
				break;
		}
	}
}
