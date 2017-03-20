package org.team401.robot.subsystems;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.team401.robot.Constants;
import org.team401.robot.ControlBoard;
import org.team401.robot.Robot;
import org.team401.robot.components.TurretRotator;
import org.team401.lib.Loop;
import org.team401.lib.DistanceSensor;
import org.team401.lib.Lidar;
import org.team401.vision.controller.VisionController;

public class Turret extends Subsystem {

    public enum TurretState {
        DISABLED, CALIBRATING, MANUAL, SENTRY, AUTO
    }

    private static Turret instance = new Turret(new Lidar(I2C.Port.kMXP, Lidar.Hardware.LIDARLITE_V3),
            new CANTalon(Constants.TURRET_ROTATOR), new CANTalon(Constants.TURRET_FEEDER),
            new Solenoid(Constants.TURRET_HOOD), new Solenoid(Constants.TURRET_LED_RING));

    private TurretState state = TurretState.DISABLED;

    private TurretRotator turretRotator;
    private Solenoid turretHood;
    private Solenoid ledRing;

    private DistanceSensor distanceSensor;

    private CANTalon feeder;

    private boolean sentryRight = false;

    private int minRPM = 1000, maxRPM = 4600;
    private int rpmOffset = 0;

    private Loop loop = new Loop() {
        @Override
        public void onStart() {
            SmartDashboard.putNumber("flywheel_user_setpoint", 0.0);
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

    private Turret(DistanceSensor distanceSensor, CANTalon turretSpinner, CANTalon turretFeeder, Solenoid turretHood, Solenoid ledRing) {
        turretRotator = new TurretRotator(turretSpinner);
        this.turretHood = turretHood;
        this.ledRing = ledRing;
        this.distanceSensor = distanceSensor;

        turretHood.set(true);
        ledRing.set(false);
        if (distanceSensor instanceof Lidar)
            ((Lidar) distanceSensor).start();

        feeder = turretFeeder;
        feeder.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        feeder.enableLimitSwitch(true, false);
        feeder.set(0.0);
    }

    /**
     * @return true if were looking directly at the turret
     */
    private boolean track() {
        if (Math.abs(Robot.getVisionDataStream().getLatestGoalYaw()) < 2) {
            turretRotator.stop();
            return true;
        }
        turretRotator.addDegrees(-Robot.getVisionDataStream().getLatestGoalYaw());
        return false;
    }

    private int getSpeedForDistance() {
        return (int) (12.348376791542*Robot.getVisionDataStream().getLatestGoalDistance() + 1959.0828756983);
    }

    private void sentry() {
        if (sentryRight) {
            turretRotator.rotate(.20);
            if (turretRotator.getPosition() < 5)
                sentryRight = false;
        } else {
            turretRotator.rotate(-.20);
            if (turretRotator.getPosition() > turretRotator.getMaxAngle()-5)
                sentryRight = true;
        }
    }

    private void run() {
        if (state == TurretState.DISABLED || state == TurretState.CALIBRATING)
            return;
        int speed = 0;
        // rotation code
        if (state.compareTo(TurretState.SENTRY) >= 0) { // auto turret control
            if (Robot.getVisionDataStream().isLatestGoalValid()) {
                if (track())
                    speed = getSpeedForDistance();
            } else {
                sentry();
            }
        } else if (state == TurretState.MANUAL) { // manual turret control
            speed = (int) SmartDashboard.getNumber("flywheel_user_setpoint", 0.0);
            double turnSpeed = ControlBoard.INSTANCE.getTurretYaw();
            int angle = ControlBoard.INSTANCE.getTurretSnapAngle();
            if (Flywheel.INSTANCE.getCurrentState() == Flywheel.FlywheelState.STOPPED) {
                if (Math.abs(turnSpeed) > .5) {
                    if (turnSpeed > 0) {
                        if (turnSpeed > .95)
                             turretRotator.rotate(.20);
                        else
                            turretRotator.rotate(.12);
                    } else {
                        if (turnSpeed < -.95)
                            turretRotator.rotate(-.20);
                        else
                            turretRotator.rotate(-.12);
                    }
                } else if (angle != -1) {
                    if (angle == 270)
                        turretRotator.setPosition(turretRotator.getMaxAngle());
                    else if (angle == 0)
                        turretRotator.setPosition(turretRotator.getMaxAngle()/2+4);
                    else if (angle == 90)
                        turretRotator.setPosition(0);
                } else {
                    turretRotator.stop();
                }
            } else {
                turretRotator.stop();
            }
        }
        // shooting code
        if (state == TurretState.AUTO && speed != 0) { // auto shooting
            Flywheel.INSTANCE.setSpeed(speed);
        } else if ((state == TurretState.SENTRY || state == TurretState.MANUAL) && ControlBoard.INSTANCE.getShootFuel().isTriggered()) { // manual shooting
            if (speed == 0)
                if (Robot.getVisionDataStream().isLatestGoalValid())
                    speed = getSpeedForDistance();
                else
                    speed = (maxRPM - minRPM) / 2;
            double delta = -ControlBoard.INSTANCE.getTurretThrottle();
            if (delta > 0.5 && speed + rpmOffset < maxRPM)
                if (delta > 0.95)
                    rpmOffset += 100;
                else
                    rpmOffset += 5;
            else if (delta < -0.5 && speed + rpmOffset > minRPM)
                if (delta < -0.95)
                    rpmOffset -= 100;
                else
                    rpmOffset -= 5;
            Flywheel.INSTANCE.setSpeed(normalizeRPM(speed + rpmOffset));
        } else { // dont shoot
            Flywheel.INSTANCE.stop();
        }

        if (Flywheel.INSTANCE.getCurrentState() == Flywheel.FlywheelState.RUNNING) {
            if (Tower.INSTANCE.getCurrentState() != Tower.TowerState.TOWER_IN)
                feeder.set(1);
        } else {
            rpmOffset = 0;
            feeder.set(0);
        }
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

    public boolean isKickerRunning() {
        return feeder.get() != 0;
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
        SmartDashboard.putNumber("turret_position", (int) turretRotator.getPosition());
        SmartDashboard.putNumber("turret_error", turretRotator.getError());
        SmartDashboard.putNumber("vision_distance", Robot.getVisionDataStream().getLatestGoalDistance());
        SmartDashboard.putNumber("vision_error", Robot.getVisionDataStream().getLatestGoalYaw());
        SmartDashboard.putNumber("lidar_distance", (int) distanceSensor.getDistance());
        SmartDashboard.putBoolean("valid_vision_data", Robot.getVisionDataStream().isLatestGoalValid());
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

