package org.team401.robot.BackupPlan;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Timer;

/**
 * Motion Magic class for 2017 robot. May be easier to test, resulting in less wasted time.
 */
public class MagicSender {
	private CANTalon talon;
	private static final double maxRPM = 0, timeout = 3;//maxRPM is a placeholder variable.  REPLACE IT ASAP!!!
	private double time;
	private double[] points;
	private int index = 0;
	private boolean finished = false;

	public MagicSender(CANTalon talon, double[] points){
		this(talon, points, 4096*0.5*maxRPM, 4096*0.01*maxRPM);
	}

	public MagicSender(CANTalon talon, double[] points, double vel, double accel){
		this.talon = talon;
		this.points = points;
		time = Timer.getFPGATimestamp();
		talon.changeControlMode(TalonControlMode.MotionMagic);
		talon.setMotionMagicCruiseVelocity(vel);
		talon.setMotionMagicAcceleration(accel);
	}
	public void control(){
		if(index == points.length) {
			finished = true;
			talon.setEncPosition(0);
			talon.changeControlMode(TalonControlMode.PercentVbus);
		}else if(time+timeout<=Timer.getFPGATimestamp()&&talon.getSetpoint()<10) {
			talon.set(points[index]);
			time = Timer.getFPGATimestamp();
			index++;
		}
	}
	public CANTalon getTalon(){
		return talon;
	}
	public boolean isFinished(){
		return finished;
	}
}