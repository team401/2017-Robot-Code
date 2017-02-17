package org.team401.robot.components;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.components.Switch;
import org.strongback.components.ui.ContinuousRange;
import org.team401.vision.VisionDataStream.VisionData;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Turret implements Runnable {

    private TurretRotator turretRotator;
    private Switch trigger;
    private Solenoid turretHood;
    private VisionDataStream stream;
    private VisionData latestData;
    private double sentryState = 0;

    public Turret(VisionDataStream stream, CANTalon turretSpinner, CANTalon flyWheelMotor1, CANTalon flyWheelMotor2,
                  CANTalon turretFeeder, Solenoid turretHood, Switch magSensor, Switch trigger, ContinuousRange yaw) {
        this.stream = stream;
        turretRotator = new TurretRotator(turretSpinner, magSensor);
        latestData = new VisionData(0, 0, 0);
        this.trigger = trigger;
        this.turretHood = turretHood;
    }

    private void track() {
        if (!trigger.isTriggered())
            turretRotator.addDegrees(-latestData.getYaw());
        else
            turretRotator.stop();
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
            if (latestData.isValid()) {
                track();
            } else
                sentryMode();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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

