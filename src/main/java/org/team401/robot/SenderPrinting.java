/**
 * Sends data to SmartDashboard for display.
 */
package org.team401.robot;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SenderPrinting {

	private static double timeout = 0;//Amount of time passed since last SD update

	private static final String []_table = {"Disabled","Enabled","Hold"};
	
	public static void OnUnderrun() {
		System.out.println("UNDERRUN");
	}
	public static void OnNoProgress() {
		System.out.println("NO PROGRESS");
	}
	private static String StrOutputEnable(CANTalon.SetValueMotionProfile sv){
		if(sv == null)
			return "null";
		if(sv.value > 3)
			return "Invalid";
		return _table[sv.value];
	}
	public static void process(CANTalon.MotionProfileStatus status1) {
		double now = Timer.getFPGATimestamp();

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