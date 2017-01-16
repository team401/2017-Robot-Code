/**
 * Since this example focuses on Motion Control, lets print everything related to MP in a clean 
 * format.  Expect to see something like......
 * 
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * outputEnable    topBufferRem    topBufferCnt    btmBufferCnt    IsValid     HasUnderrun      IsUnderrun          IsLast         VelOnly         targPos         targVel
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * outputEnable    topBufferRem    topBufferCnt    btmBufferCnt    IsValid     HasUnderrun      IsUnderrun          IsLast         VelOnly         targPos         targVel
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * Hold            2048            0               0               1                                                                               5.0             0.0
 * 
 * ...where the columns are reprinted occasionally so you know whats up.
 * 
 * 
 * 
 */
package org.team401.robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class instrumentation {

	static double timeout = 0;

	private static final String []_table = {"Disabled","Enabled","Hold"};
	
	public static void OnUnderrun() {
		System.out.format("%s\n", "UNDERRUN");
	}
	public static void OnNoProgress() {
		System.out.format("%s\n", "NOPROGRESS");
	}
	static private String StrOutputEnable(CANTalon.SetValueMotionProfile sv)
	{
		if(sv == null)
			return "null";
		if(sv.value > 3)
			return "Inval";
		return _table[sv.value];
	}
	public static void process(CANTalon.MotionProfileStatus status1) {
		double now = edu.wpi.first.wpilibj.Timer.getFPGATimestamp();

		if((now-timeout) > 0.2){
			timeout = now;
			SmartDashboard.putNumber("Top Buffer Count", status1.topBufferCnt);
			SmartDashboard.putNumber("Bottom Buffer Count", status1.btmBufferCnt);
			SmartDashboard.putString("Output", StrOutputEnable(status1.outputEnable));
			SmartDashboard.putBoolean("Has Underrun", status1.hasUnderrun);
			SmartDashboard.putBoolean("Is Underrun", status1.isUnderrun);
			SmartDashboard.putBoolean("Is Valid", status1.activePointValid);
			SmartDashboard.putBoolean("Is Last Point", status1.activePoint.isLastPoint);
			SmartDashboard.putBoolean("Velocity Only?", status1.activePoint.velocityOnly);
			SmartDashboard.putNumber("Position", status1.activePoint.position);
			SmartDashboard.putNumber("Velocity", status1.activePoint.velocity);
		}
	}
}