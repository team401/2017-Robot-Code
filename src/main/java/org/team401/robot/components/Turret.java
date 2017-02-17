package org.team401.robot.components;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.components.Switch;
import org.strongback.components.ui.ContinuousRange;
import org.team401.robot.MathUtils;
import org.team401.vision.VisionDataStream.VisionData;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Turret implements Runnable {

    private TurretRotator turretRotator;
    private Solenoid turretHood;

    private VisionDataStream stream;
    private VisionData latestData;
    private Switch trigger;

    private CANTalon flywheel, feeder;

    private ContinuousRange yaw, throttle;
    private double sensitivity;
    private double sentryState = 0;
    private boolean isSentryEnabled;

    public Turret(VisionDataStream stream, CANTalon turretSpinner, CANTalon flyWheelMotor1, CANTalon flyWheelMotor2,
                  CANTalon turretFeeder, Solenoid turretHood, Switch magSensor, Switch trigger,
                  ContinuousRange yaw, ContinuousRange throttle, double sensitivity) {
        this.stream = stream;
        turretRotator = new TurretRotator(turretSpinner, magSensor);
        latestData = new VisionData(0, 0, 0);
        this.trigger = trigger;
        this.turretHood = turretHood;
        this.isSentryEnabled = true;
        this.yaw = yaw;
        this.throttle = throttle;
        this.sensitivity = sensitivity;

        flywheel = flyWheelMotor2;

        flyWheelMotor1.setSafetyEnabled(false);
        flyWheelMotor1.changeControlMode(CANTalon.TalonControlMode.Follower);
        flyWheelMotor1.set(flywheel.getDeviceID());
        flyWheelMotor1.setInverted(true);
        flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        flywheel.configPeakOutputVoltage(12, 0);
        flywheel.setSafetyEnabled(false);
        flywheel.set(0);
        flywheel.setP(1);
        flywheel.setI(0);
        flywheel.setD(0);
        flywheel.setF(0);

        feeder = turretFeeder;
        feeder.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        feeder.set(0.0);
    }

    /**
     *
     * @return true if were looking directly at the turret
     */
    private boolean track() {
        if (Math.abs(latestData.getYaw()) < 2) {
            turretRotator.stop();
            return true;
        }
        turretRotator.addDegrees(-latestData.getYaw());
        return false;
    }

    private double getSpeedForDistance() {
        //TODO: Lidar shit
        return 0.0;
    }

    private void sentryMode() {
        if (turretRotator.getPosition() >= turretRotator.getMaxAngle())
            sentryState = 0;
        else if (turretRotator.getPosition() <= 0)
            sentryState = turretRotator.getMaxAngle();
        turretRotator.setPosition(sentryState);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            latestData = stream.getLatestGoalData();
            double speed = 0.0;
            if (isSentryEnabled) {
                if (latestData.isValid()) {
                    flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
                    if (track())
                        speed = getSpeedForDistance();
                } else {
                    flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                    sentryMode();
                }
            } else { // manual turret control
                flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
                turretRotator.getRotator().changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                double turnSpeed = yaw.read();
                if (Math.abs(turnSpeed) > .1)
                    if (turnSpeed > 0)
                        turretRotator.getRotator()
                                .set(MathUtils.INSTANCE.toRange(turnSpeed, .1, 1, .25, .75));
                    else
                        turretRotator.getRotator()
                                .set(-MathUtils.INSTANCE.toRange(Math.abs(turnSpeed), .1, 1, .25, .75));
                speed = MathUtils.INSTANCE.toRange(throttle.read()*-1, -1, 1, 1000, 4500);
            }
            //TODO: auto shooting
            // manual shooting
            if (trigger.isTriggered()) {
                flywheel.set(speed);

            } else {
                flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                flywheel.set(0);
                feeder.set(.75);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void enableSentry(boolean enabled) {
        isSentryEnabled = enabled;
    }

    public boolean isSentryEnabled() {
        return isSentryEnabled;
    }

    public void extendHood(boolean extended) {
        turretHood.set(extended);
    }

    public boolean isHoodExtended() {
        return turretHood.get();
    }

    public TurretRotator getTurretRotator() {
        return turretRotator;
    }
}

