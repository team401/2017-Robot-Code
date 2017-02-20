package org.team401.robot.components;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.Switch;
import org.strongback.components.ui.ContinuousRange;
import org.team401.robot.Constants;
import org.team401.robot.Robot;
import org.team401.robot.loops.Loop;
import org.team401.robot.math.MathUtils;
import org.team401.robot.sensors.DistanceSensor;
import org.team401.vision.VisionDataStream.VisionData;

public class Turret {

    private TurretRotator turretRotator;
    private Solenoid turretHood;
    private Solenoid ledRing;

    private VisionData latestData;
    private DistanceSensor distanceSensor;
    private Switch trigger;

    private CANTalon flywheel, flywheelSlave, feeder;

    private ContinuousRange yaw, throttle;
    private boolean isSentryEnabled, autoShootingEnabled;

    private Loop loop = new Loop() {
        @Override
        public void onStart() {
            enableSentry(true);
        }

        @Override
        public void onLoop() {
            run();
            printToSmartDashboard();
        }

        @Override
        public void onStop() {
            enableSentry(false);
        }
    };

    public Turret(DistanceSensor distanceSensor, CANTalon turretSpinner, CANTalon flyWheelMotor1,
                  CANTalon flyWheelMotor2, CANTalon turretFeeder, Solenoid turretHood, Solenoid ledRing,
                  Switch trigger, ContinuousRange yaw, ContinuousRange throttle) {
        turretRotator = new TurretRotator(turretSpinner);
        latestData = new VisionData(0, 0, 0);
        this.trigger = trigger;
        this.turretHood = turretHood;
        this.ledRing = ledRing;
        this.isSentryEnabled = true;
        this.yaw = yaw;
        this.throttle = throttle;
        this.distanceSensor = distanceSensor;

        turretHood.set(false);
        ledRing.set(false);

        flywheel = flyWheelMotor2;

        flywheelSlave = flyWheelMotor1;
        flywheelSlave.setSafetyEnabled(false);
        flywheelSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        flywheelSlave.set(flywheel.getDeviceID());
        flywheelSlave.setInverted(true);
        flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
        flywheel.configPeakOutputVoltage(12, 0);
        flywheel.setSafetyEnabled(false);
        flywheel.set(0);
        flywheel.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
                Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0);

        feeder = turretFeeder;
        feeder.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        feeder.set(0.0);
    }

    /**
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
            turretRotator.setPosition(turretRotator.getMaxAngle() + 1);
    }

    private void run() {
        latestData = Robot.getVisionDataStream().getLatestGoalData();
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
            speed = MathUtils.INSTANCE.toRange(throttle.read() * -1, -1, 1, 1000, 4500);
        }
        // shooting code
        if (autoShootingEnabled && speed != 0) { // auto shooting
            flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
            flywheel.set(speed);
            feeder.set(.75);
        } else if (!autoShootingEnabled && trigger.isTriggered()) { // manual shooting
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

    public void enableAutoShooting(boolean enabled) {
        autoShootingEnabled = enabled;
        if (enabled && !isSentryEnabled)
            enableSentry(true);
    }

    public boolean isAutoShootingEnabled() {
        return autoShootingEnabled;
    }

    public void enableSentry(boolean enabled) {
        isSentryEnabled = enabled;
        ledRing.set(enabled);
        if (!enabled && autoShootingEnabled)
            enableAutoShooting(false);
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

    public Loop getTurretLoop() {
        return loop;
    }

    public Switch atZeroPoint() {
        return () -> feeder.isFwdLimitSwitchClosed();
    }

    private void printToSmartDashboard() {
        SmartDashboard.putNumber("flywheel_velocity", flywheel.getSpeed());
        SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());
        SmartDashboard.putNumber("turret_position", turretRotator.getPosition());
        SmartDashboard.putNumber("turret_error", turretRotator.getRotator().getClosedLoopError());
        SmartDashboard.putBoolean("turret_hood_extended", turretHood.get());
        SmartDashboard.putBoolean("limit_switch_triggered", atZeroPoint().isTriggered());
        SmartDashboard.putBoolean("sentry_enabled", isSentryEnabled());
        SmartDashboard.putBoolean("auto_shooting_enabled", isAutoShootingEnabled());
    }
}

