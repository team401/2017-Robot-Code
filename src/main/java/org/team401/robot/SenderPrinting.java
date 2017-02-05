package org.team401.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.SetValueMotionProfile;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Sends data to SmartDashboard for display.
 * With 4 different ProfileSenders doing this at the same time, we will probably have a bunch of race conditions.
 * I'll probably delete this class eventually.
 */

public class SenderPrinting {
	//Amount of time passed since last SD update
	private static double timeout = 0;
	
	//Talon status options.
	private static final String [] statuses = {"Disabled","Enabled","Hold"};

	/**
	 * Call this when the loop is underrun.
	 */
	public static void OnUnderrun() {
		System.out.println("UNDERRUN");
	}

	/**
	 * Call this when there hasn't been any progress.
	 */
	public static void OnNoProgress() {
		System.out.println("NO PROGRESS");
	}

	/**
	 * Selects a name for the talon's set value
	 * @param sv Set value of the talon
	 * @return Name of the current set value
	 */
	private static String setValueString(SetValueMotionProfile sv){
		if(sv == null)
			return "null";
		if(sv.value > 3)
			return "Invalid";
		return statuses[sv.value];
	}

	/**
	 * Sends a bunch of data every 200ms.
	 * @param status The status object used by the Talon
	 */
	public static void process(CANTalon.MotionProfileStatus status) {
		//Get the current time and compare to when we last sent data
		double now = Timer.getFPGATimestamp();
		if((now-timeout) > 0.2){
			//Send new data and reset the timer
			timeout = now;
			SmartDashboard.putNumber("Top Buffer Count", status.topBufferCnt);
			SmartDashboard.putNumber("Bottom Buffer Count", status.btmBufferCnt);
			SmartDashboard.putString("Output", setValueString(status.outputEnable));
			SmartDashboard.putBoolean("Has Underrun", status.hasUnderrun);
			SmartDashboard.putBoolean("Is Underrun", status.isUnderrun);
			SmartDashboard.putBoolean("Is Valid", status.activePointValid);
			SmartDashboard.putBoolean("Is Last Point", status.activePoint.isLastPoint);
			SmartDashboard.putBoolean("Velocity Only?", status.activePoint.velocityOnly);
			SmartDashboard.putNumber("Position", status.activePoint.position);
			SmartDashboard.putNumber("Velocity", status.activePoint.velocity);
		}
	}
}