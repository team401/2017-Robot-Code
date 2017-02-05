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

    //SmartDashboard outputs to monitor this class are questionable.  This lets you disable them.
    private boolean alerts;

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

    //alerts defaults to true
    public ProfileSender(CANTalon talon, double[][] profile){
        this(talon, profile, true);
    }

    /**
     * Constructor.  Also starts the PeriodicRunnable.
     * @param talon The Talon SRX to send data to
     * @param profile The motion profile to send
     * @param alerts Do we really want 10 things on SmartDashboard for this class?
     */
    public ProfileSender(CANTalon talon, double[][] profile, boolean alerts) {
        //Save instance data
        this.talon = talon;
        this.profile = profile;
        this.alerts = alerts;

        //Change the control period and notifier rate to half the Java rate
        this.talon.changeMotionControlFramePeriod((int)(profile[0][2]/2));
        _notifier.startPeriodic(profile[0][2]/2000);
    }
	public void resetEncoder(){
	    talon.setEncPosition(0);
    }



    public void setProfile(double[][] profile){
    	this.profile = profile;
	}

    /**
     * Called to clear Motion profile buffer and reset state info during
     * disabled and when Talon is not in MP control mode.
     */
    public void reset() {
		/*
		 * Let's clear the buffer just in case user decided to disable in the
		 * middle of an MP, and now we have the second half of a profile just
		 * sitting in memory.
		 */
        talon.clearMotionProfileTrajectories();
		/* When we do re-enter motionProfile control mode, stay disabled. */
        setValue = SetValueMotionProfile.Disable;
		/* When we do start running our state machine start at the beginning. */
        state = 0;
        timeoutCount = -1;
		/*
		 * If application wanted to start an MP before, ignore and wait for next
		 * button press
		 */
        startSignal = false;
    }

    /**
     * Called every loop.
     */
    public void control() {
		/* Get the motion profile status every loop */
        talon.getMotionProfileStatus(status);


		/*
		 * track time, this is rudimentary but that's okay, we just want to make
		 * sure things never get stuck.
		 */
        if (!(timeoutCount < 0))
			/* our timeout is nonzero */
            if (timeoutCount == 0)
				/*
				 * something is wrong. Talon is not present, unplugged, breaker
				 * tripped
				 */
                if(alerts)
                    SenderPrinting.OnNoProgress();
            else
                timeoutCount--;



		/* first check if we are in MP mode */
        if (talon.getControlMode() != TalonControlMode.MotionProfile) {
			/*
			 * we are not in MP mode. We are probably driving the robot around
			 * using gamepads or some other mode.
			 */
            state = 0;
            timeoutCount = -1;
        } else {
			/*
			 * we are in MP control mode. That means: starting Mps, checking Mp
			 * progress, and possibly interrupting MPs if thats what you want to
			 * do.
			 */
            switch (state) {
                case 0: /* wait for application to tell us to start an MP */
                    if (startSignal) {
                        startSignal = false;

                        setValue = SetValueMotionProfile.Disable;
                        startFilling(profile);
						/*
						 * MP is being sent to CAN bus, wait a small amount of time
						 */
                        state = 1;
                        timeoutCount = timeoutAmt;
                    }
                    break;
                case 1: /*
						 * wait for MP to stream to Talon, really just the first few
						 * points
						 */
					/* do we have a minimum numberof points in Talon */
                    if (status.btmBufferCnt > minPointsSent) {
						/* start (once) the motion profile */
                        setValue = SetValueMotionProfile.Enable;
						/* MP will start once the control frame gets scheduled */
                        state = 2;
                        timeoutCount = timeoutAmt;
                    }
                    break;
                case 2: /* check the status of the MP */
					/*
					 * if talon is reporting things are good, keep adding to our
					 * timeout. Really this is so that you can unplug your talon in
					 * the middle of an MP and react to it.
					 */
                    if (!status.isUnderrun) {
                        timeoutCount = timeoutAmt;
                    }
					/*
					 * If we are executing an MP and the MP finished, start loading
					 * another. We will go into hold state so robot servo's
					 * position.
					 */
                    if (status.activePointValid && status.activePoint.isLastPoint) {
						/*
						 * because we set the last point's isLast to true, we will
						 * get here when the MP is done
						 */
                        setValue = SetValueMotionProfile.Hold;
                        state = 0;
                        timeoutCount = -1;
                    }
                    break;
            }
        }
		/* printfs and/or logging */
        if(alerts)
            SenderPrinting.process(status);
    }

    /**
     * Start filling the MPs to all of the involved Talons.
     */
    public void startFilling(double[][] profile) {
        startFilling(profile, profile.length);
    }

    public void startFilling(double[][] profile, int totalCnt) {

		/* create an empty point */
        TrajectoryPoint point = new TrajectoryPoint();

		/* did we get an underrun condition since last time we checked ? */
        if (status.hasUnderrun) {
			/* better log it so we know about it */
            if(alerts)
			    SenderPrinting.OnUnderrun();
			/*
			 * clear the error. This flag does not auto clear, this way 
			 * we never miss logging it.
			 */
            talon.clearMotionProfileHasUnderrun();
        }
		/*
		 * just in case we are interrupting another MP and there is still buffer
		 * points in memory, clear it.
		 */
        talon.clearMotionProfileTrajectories();

		/* This is fast since it's just into our TOP buffer */
        for (int i = 0; i < totalCnt; i++) {
			/* for each point, fill our structure and pass it to API */
            point.position = profile[i][0];
            point.velocity = profile[i][1];
            point.timeDurMs = (int) profile[i][2];
            point.profileSlotSelect = 0; /* which set of gains would you like to use? */
            point.velocityOnly = false; /* set true to not do any position
										 * servo, just velocity feedforward
										 */
            point.zeroPos = i == 0; /* set this to true on the first point */
            point.isLastPoint = (i + 1) == totalCnt;/* set this to true on the last point  */
            talon.pushMotionProfileTrajectory(point);
        }
        System.out.println("Done streaming!");
    }

    /**
     * Called by application to signal Talon to start the buffered MP (when it's
     * able to).
     */
    public void startMotionProfile() {
        startSignal = true;
    }

    /**
     * @return the output value to pass to Talon's set() routine. 0 for disable
     * motion-profile output, 1 for enable motion-profile, 2 for hold
     * current motion profile trajectory point.
     */
    SetValueMotionProfile getSetValue() {
        return setValue;
    }
    CANTalon getTalon(){
        return talon;
    }
}