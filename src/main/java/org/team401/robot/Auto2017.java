package org.team401.robot;

import com.ctre.CANTalon;

/**
 * Created by Driver Station on 2/3/2017.
 */
public class Auto2017 {
	private String start, tgt;
	private boolean mecanum;
	private int state = 0;
	private double[][][] profiles;

	MotionProfileExample x = new MotionProfileExample(new CANTalon(0));
	public Auto2017(String start, String tgt, boolean mecanum){
		this.start = start;
		this.tgt = tgt;
		this.mecanum = mecanum;
		profiles = GeneratedMotionProfiles.getProfile(start, tgt, mecanum);
	}
	public void periodic(){
		switch(state){
			case 0:
				x.control();
				if(x.getSetValue().equals(CANTalon.SetValueMotionProfile.Hold))
					state = 1;
				break;
			case 1:

		}
	}
}
