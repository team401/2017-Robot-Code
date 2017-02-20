package org.team401.robot.components;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.Switch;
import org.strongback.components.ui.ContinuousRange;
import org.team401.robot.MathUtils;
import org.team401.robot.sensors.DistanceSensor;
import org.team401.vision.VisionDataStream.VisionData;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Turret implements Runnable {

    private TurretRotator turretRotator;
    private Solenoid turretHood;
    private Solenoid ledRing;

    private VisionDataStream stream;
    private VisionData latestData;
    private DistanceSensor distanceSensor;
    private Switch trigger;

    private CANTalon flywheel, feeder;

    private ContinuousRange yaw, throttle;
    private boolean isSentryEnabled, autoShootingEnabled;

    public Turret(VisionDataStream stream, DistanceSensor distanceSensor, CANTalon turretSpinner,
                  CANTalon flyWheelMotor1, CANTalon flyWheelMotor2, CANTalon turretFeeder,
                  Solenoid turretHood, Solenoid ledRing, Switch magSensor, Switch trigger,
                  ContinuousRange yaw, ContinuousRange throttle) {
        this.stream = stream;
        turretRotator = new TurretRotator(turretSpinner, magSensor);
        latestData = new VisionData(0, 0, 0);
        this.trigger = trigger;
        this.turretHood = turretHood;
        this.isSentryEnabled = true;
        this.yaw = yaw;
        this.throttle = throttle;
        this.distanceSensor = distanceSensor;

        turretHood.set(false);
        ledRing.set(false);

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
        double distance = distanceSensor.getDistance();
        return 0.0;
    }

    private void sentryMode() {
        if (turretRotator.getPosition() >= turretRotator.getMaxAngle())
            turretRotator.setPosition(-1);
        else if (turretRotator.getPosition() <= 0)
            turretRotator.setPosition(turretRotator.getMaxAngle()+1);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            latestData = stream.getLatestGoalData();
            SmartDashboard.putBoolean("Can See Goal", latestData.isValid());
            SmartDashboard.putNumber("Distance to High Goal", distanceSensor.getDistance());
            double speed = 0.0;
            // rotation code
            if (isSentryEnabled) { // auto turret control
                if (latestData.isValid()) {
                    if (track())
                        speed = getSpeedForDistance();
                } else {
                    sentryMode();
                }
            } else { // manual turret control
                turretRotator.getRotator().changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                double turnSpeed = yaw.read();
                if (Math.abs(turnSpeed) > .1)
                    if (turnSpeed > 0)
                        turretRotator.getRotator()
                                .set(MathUtils.INSTANCE.toRange(turnSpeed, .1, 1, .25, .75));
                    else
                        turretRotator.getRotator()
                                .set(-MathUtils.INSTANCE.toRange(-turnSpeed, .1, 1, .25, .75));
                speed = MathUtils.INSTANCE.toRange(throttle.read()*-1, -1, 1, 1000, 4500);
            }
            // shooting code
            if (autoShootingEnabled && speed != 0) { // auto shooting
                flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
                flywheel.set(speed);
                feeder.set(.75);
            }
            else if (!autoShootingEnabled && trigger.isTriggered()) { // manual shooting
                flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
                flywheel.set(speed);
                feeder.set(.75);
            } else { // dont shoot
                flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
                flywheel.set(0);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void enableAutoShooting(boolean enabled) {
        autoShootingEnabled = enabled;
    }

    public boolean isAutoShootingEnabled() {
        return autoShootingEnabled;
    }

    public void enableSentry(boolean enabled) {
        isSentryEnabled = enabled;
        ledRing.set(isSentryEnabled);

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

