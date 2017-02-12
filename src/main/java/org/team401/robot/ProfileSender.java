package org.team401.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.MotionProfileStatus;
import com.ctre.CANTalon.SetValueMotionProfile;
import com.ctre.CANTalon.TrajectoryPoint;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Notifier;

/**
 * Pushes motion profiles to a Talon and notifies the main program when the profile is over.
 */

public class ProfileSender {
	//The Talon SRX we are sending profiles to
	private CANTalon talon;

	//Manual multipliers for position and velocity if the path was a bit off
	public static double posMult, velMult;

	//Current status of the Talon
	private MotionProfileStatus status = new MotionProfileStatus();

	//The profile to move along
	private double[][] profile;

	//Current state in control()'s state machine
	private int state = 0;

	//State machine timeout counter.  -1 is disabled.  >0 counts down each loop.  At 0, an error message is printed.
	private int timeoutCount = -1;

	//Number of loops to timeout when timeout is needed
	private static final int timeoutAmt = 10;

	//Signal to start the motion profile
	private boolean startSignal = false;

	//The Talon's set value
	private SetValueMotionProfile setValue = SetValueMotionProfile.Disable;

	//Number of points that have to be sent before the profile will start
	private static final int minPointsSent = 5;

	/**
	 * Periodically tells the Talon to process sent profile points
	 */
	private class PeriodicRunnable implements java.lang.Runnable {
		public void run() {
			talon.processMotionProfileBuffer();
		}
	}

	//Notifier to run the above class
	private Notifier _notifier = new Notifier(new PeriodicRunnable());

	/**
	 * Constructor.  Also starts the PeriodicRunnable.
	 * @param talon The Talon SRX to send data to
	 * @param profile The motion profile to send
	 */
	public ProfileSender(CANTalon talon, double[][] profile) {
		//Save instance data
		this.talon = talon;
		this.profile = profile;

		//Change the control period and notifier rate to half the Java rate
		this.talon.changeMotionControlFramePeriod((int)(profile[0][2]/2));
		_notifier.startPeriodic(profile[0][2]/2000);
	}

	/**
	 * Sets the encoder on the Talon to 0.
	 * Should be called when the profile is done so position values will be fresh for the next.
	 */
	public void resetEncoder(){
		talon.setEncPosition(0);
	}

	/**
	 * Called to clear motion profile buffer and reset state info during
	 * disabled and when Talon is not in MP control mode.
	 */
	public void reset() {
		//Clear the Talon's buffer
		talon.clearMotionProfileTrajectories();

		//Set instance data to the defaults
		setValue = SetValueMotionProfile.Disable;
		state = 0;
		timeoutCount = -1;
		startSignal = false;
	}

	/**
	 * Call this every loop.
	 */
	public void control() {
		//Update the Talon's status
		talon.getMotionProfileStatus(status);

		//Timeout tracker will send a No Progress message if stuff isn't going on fast enough.
		if (timeoutCount >= 0)
			if (timeoutCount == 0) {
				//Something must have gone wrong!
				System.out.println("NO PROGRESS.  CURRENT POINT "+(status.activePointValid?"VALID":"INVALID"));
			}
			else
				//Wait until unsafe latency is detected
				timeoutCount--;

		//Check if we are motion profiling
		if (talon.getControlMode() != TalonControlMode.MotionProfile) {
			//Not profiling right now, so reset the state machine and do nothing.
			state = 0;
			timeoutCount = -1;
		} else {
			//Talon is configured to profile, so move through the process of sending a profile.
			switch (state) {
				case 0:
					//Wait here until told to start
					if (startSignal) {
						//Reset for next time
						startSignal = false;

						//Disable the profile until we have enough points in
						setValue = SetValueMotionProfile.Disable;

						//Send points and progress state machine
						startFilling(profile);
						state++;
						timeoutCount = timeoutAmt;
					}
					break;
				case 1:
					//Wait until enough points are in
					if (status.btmBufferCnt > minPointsSent) {
						//Enable the motors
						setValue = SetValueMotionProfile.Enable;

						//Progress to next state
						state++;
						timeoutCount = timeoutAmt;
					}
					break;
				case 2:
					//As long as everything is alright, never timeout.
					if (!status.isUnderrun) {
						timeoutCount = timeoutAmt;
					}

					//Stop everything if the profile is over.
					if (status.activePointValid && status.activePoint.isLastPoint) {
						//Hold mode keeps motor in place and can be used as an external signal
						setValue = SetValueMotionProfile.Hold;

						//Reset to beginning state in machine.
						state = 0;
						timeoutCount = -1;
					}
					break;
				default:
					//Error message if state machine breaks
					System.out.println("Invalid ProfileSender state!");
					break;
			}
		}
		talon.set(setValue.value);
	}

	//totalCount defaults to the length of the profile
	public void startFilling(double[][] profile) {
		startFilling(profile, profile.length);
	}

	/**
	 * Start filling the MPs to all of the involved Talons.
	 */
	public void startFilling(double[][] profile, int totalCount) {
		//create an empty point
		TrajectoryPoint point = new TrajectoryPoint();

		//did we get an underrun condition since last time we checked?
		if (status.hasUnderrun) {
			//If so, tell the DS.
				System.out.println("UNDERRUN");

			//Clear error
			talon.clearMotionProfileHasUnderrun();
		}

		//Remove previous MP in case we interrupt
		talon.clearMotionProfileTrajectories();

		// This is fast since it's just into our top buffer
		for (int i = 0; i < totalCount; i++) {
			//Fill up the point since the constructor is empty
			point.position = profile[i][0]*posMult;
			point.velocity = profile[i][1]*velMult;
			point.timeDurMs = (int) profile[i][2];

			//Talon supports multiple saved PID profiles.  Just use the first.
			point.profileSlotSelect = 0;

			//yes we want to use the position data
			point.velocityOnly = false;

			//Define the first and last points
			point.zeroPos = i == 0;
			point.isLastPoint = (i + 1) == totalCount;
			if(i==0)
				System.out.println(point.position+", "+point.velocity+", "+point.zeroPos);

			//Push to the Talon
			talon.pushMotionProfileTrajectory(point);
			System.out.println("Buffer Count: "+talon.getMotionProfileTopLevelBufferCount());
		}

		//Print our success at the end
		System.out.println("Done streaming!");
	}

	/**
	 * Call this to trigger the profile when ready.
	 */
	public void startMotionProfile() {
		startSignal = true;
	}

	/**
	 * Gets the set value so we know if the loop is disabled, enabled, or holding.
	 *
	 * @return Set value.  "Hold" or 2 if finished profiling.
	 */
	public SetValueMotionProfile getSetValue() {
		return setValue;
	}

	/**
	 * Getter for the Talon so you don't have to dig as deep to get the reference
	 */
	public CANTalon getTalon(){
		return talon;
	}
}