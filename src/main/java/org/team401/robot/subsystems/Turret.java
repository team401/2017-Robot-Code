package org.team401.robot.subsystems;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.robot.Constants;
import org.team401.robot.ControlBoard;
import org.team401.robot.Robot;
import org.team401.robot.components.TurretRotator;
import org.team401.robot.loops.Loop;
import org.team401.lib.MathUtils;
import org.team401.lib.DistanceSensor;
import org.team401.lib.Lidar;
import org.team401.vision.VisionDataStream.VisionData;
import org.team401.vision.controller.VisionController;

public class Turret extends Subsystem {

    public enum TurretState {
        DISABLED, CALIBRATING, MANUAL, SENTRY, AUTO
    }

    private static Turret instance = new Turret(new Lidar(I2C.Port.kMXP, Lidar.Hardware.LIDARLITE_V3),
            new CANTalon(Constants.TURRET_ROTATOR), new CANTalon(Constants.TURRET_FLYWHEEL_MASTER),
            new CANTalon(Constants.TURRET_FLYWHEEL_SLAVE), new CANTalon(Constants.TURRET_FEEDER),
            new Solenoid(Constants.TURRET_HOOD), new Solenoid(Constants.TURRET_LED_RING));

    private TurretState state = TurretState.DISABLED;

    private TurretRotator turretRotator;
    private Solenoid turretHood;
    private Solenoid ledRing;

    private VisionData latestData;
    private DistanceSensor distanceSensor;

    private CANTalon flywheel, flywheelSlave, feeder;

    private boolean isFiring = false;
    private boolean sentryRight = false;

    private int minRPM = 1000, maxRPM = 4600;
    private int rpmOffset = 0;

    private Loop loop = new Loop() {
        @Override
        public void onStart() {

        }

        @Override
        public void onLoop() {
            if (state.compareTo(TurretState.MANUAL) > 0)
                ledRing.set(true);
            else
                ledRing.set(false);
            run();
        }

        @Override
        public void onStop() {
            setWantedState(TurretState.MANUAL);
            turretRotator.stop();
        }
    };

    private Turret(DistanceSensor distanceSensor, CANTalon turretSpinner, CANTalon flyWheelMaster,
                  CANTalon flywheelSlave, CANTalon turretFeeder, Solenoid turretHood, Solenoid ledRing) {
        turretRotator = new TurretRotator(turretSpinner);
        latestData = new VisionData(0, 0, 0);
        this.turretHood = turretHood;
        this.ledRing = ledRing;
        this.distanceSensor = distanceSensor;

        turretHood.set(true);
        ledRing.set(false);
        if (distanceSensor instanceof Lidar)
            ((Lidar) distanceSensor).start();

        this.flywheelSlave = flywheelSlave;
        flywheelSlave.setSafetyEnabled(false);
        flywheelSlave.changeControlMode(CANTalon.TalonControlMode.Follower);
        flywheelSlave.set(flyWheelMaster.getDeviceID());
        flywheel = flyWheelMaster;
        flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        flywheel.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative);
        flywheel.set(0);
        flywheel.reverseOutput(true);
        //flywheel.reverseSensor(true);
        flywheel.setInverted(true);
        flywheel.setSafetyEnabled(false);
        flywheel.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
                Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0);

        feeder = turretFeeder;
        feeder.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        feeder.enableLimitSwitch(true, false);
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

    private int getSpeedForDistance() {
        double distance = distanceSensor.getDistance();
        return 0;
    }

    private void sentry() {
        if (sentryRight) {
            turretRotator.rotate(.08);
            if (turretRotator.getPosition() < 5)
                sentryRight = false;
        } else {
            turretRotator.rotate(-.08);
            if (turretRotator.getPosition() > turretRotator.getMaxAngle()-5)
                sentryRight = true;
        }
    }

    private void run() {
        if (state == TurretState.DISABLED || state == TurretState.CALIBRATING)
            return;
        latestData = Robot.getVisionDataStream().getLatestGoalData();
        int speed = 0;
        // rotation code
        if (state.compareTo(TurretState.SENTRY) >= 0) { // auto turret control
            if (latestData.isValid()) {
                if (track())
                    speed = getSpeedForDistance();
            } else {
                sentry();
            }
        } else if (state == TurretState.MANUAL) { // manual turret control
            double turnSpeed = ControlBoard.INSTANCE.getTurretYaw();
            int angle = ControlBoard.INSTANCE.getTurretSnapAngle();
            if (Math.abs(turnSpeed) > .5) {
                if (turnSpeed > 0) {
                    if (turnSpeed > .95)
                        turretRotator.rotate(.10);
                    else
                        turretRotator.rotate(.04);
                } else {
                    if (turnSpeed < -.95)
                        turretRotator.rotate(-.10);
                    else
                        turretRotator.rotate(-.04);
                }
            } else if (angle != -1) {
                if (angle == 270)
                    turretRotator.setPosition(turretRotator.getMaxAngle());
                else if (angle == 0)
                    turretRotator.setPosition(turretRotator.getMaxAngle()/2+5);
                else if (angle == 90)
                    turretRotator.setPosition(0);
            } else {
                turretRotator.rotate(0);
            }
        }
        // shooting code
        if (state == TurretState.AUTO && speed != 0) { // auto shooting
            isFiring = true;
        } else if ((state == TurretState.SENTRY || state == TurretState.MANUAL) && ControlBoard.INSTANCE.getShootFuel().isTriggered()) { // manual shooting
            isFiring = true;
        } else { // dont shoot
            isFiring = false;
        }

        if (isFiring) {
            configTalonsForSpeedControl();
            if (speed == 0)
                speed = (maxRPM - minRPM) / 2;
            double delta = ControlBoard.INSTANCE.getTurretThrottle();
            if (delta > 0)
                if (delta > .95)
                    rpmOffset += 100;
                else
                    rpmOffset += 10;
            else
                if (delta < -.95)
                    rpmOffset -= 100;
                else
                    rpmOffset -= 10;
            flywheel.set(normalizeRPM(speed + rpmOffset));
            if (GearHolder.INSTANCE.getCurrentState() != GearHolder.GearHolderState.TOWER_IN)
                feeder.set(1);
        } else {
            rpmOffset = 0;
            flywheel.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
            flywheel.set(0);
            feeder.set(0);
        }
    }

    private void configTalonsForSpeedControl() {
        flywheel.changeControlMode(CANTalon.TalonControlMode.Speed);
        flywheel.setProfile(0);
    }

    public void zeroSensors() {
        turretRotator.zero();
    }

    public void setWantedState(TurretState state) {
        if (state.compareTo(TurretState.SENTRY) >= 0)
            Robot.getVisionController().setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.PROCESSING);
        else
            Robot.getVisionController().setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.STREAMING);
        this.state = state;
    }

    public TurretState getCurrentState() {
        return state;
    }

    public void extendHood(boolean extended) {
        if (state == TurretState.MANUAL || state == TurretState.SENTRY)
            turretHood.set(extended);
    }

    public boolean isHoodExtended() {
        return turretHood.get();
    }

    public TurretRotator getTurretRotator() {
        return turretRotator;
    }

    public boolean atZeroPoint() {
        return feeder.isRevLimitSwitchClosed();
    }

    public boolean isFiring() {
        return isFiring;
    }

    private int normalizeRPM(int speed) {
        if (speed > maxRPM)
            return maxRPM;
        if (speed < minRPM)
            return minRPM;
        return speed;
    }

    @Override
    public void printToSmartDashboard() {
        SmartDashboard.putNumber("flywheel_rpm", (int) flywheel.getSpeed());
        SmartDashboard.putNumber("flywheel_error", flywheel.getClosedLoopError());
        SmartDashboard.putNumber("turret_position", (int) turretRotator.getPosition());
        SmartDashboard.putNumber("turret_error", turretRotator.getError());
        SmartDashboard.putNumber("vision_distance", latestData.getDistance());
        SmartDashboard.putNumber("vision_error", latestData.getYaw());
        SmartDashboard.putNumber("lidar_distance", (int) distanceSensor.getDistance());
        SmartDashboard.putBoolean("valid_vision_data", latestData.isValid());
        SmartDashboard.putBoolean("turret_on_target", turretRotator.onTarget());
        SmartDashboard.putBoolean("turret_hood_extended", turretHood.get());
        SmartDashboard.putBoolean("limit_switch_triggered", atZeroPoint());
        SmartDashboard.putBoolean("sentry_enabled", state.compareTo(TurretState.SENTRY) >= 0);
        SmartDashboard.putBoolean("auto_shooting_enabled", state == TurretState.AUTO);
        SmartDashboard.putBoolean("turret_enabled", state != TurretState.DISABLED);
    }

    public Loop getSubsystemLoop() {
        return loop;
    }

    public static Turret getInstance() {
        return instance;
    }
}

