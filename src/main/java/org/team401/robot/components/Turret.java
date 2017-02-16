package org.team401.robot.components;

import com.ctre.CANTalon;
import org.strongback.components.Switch;
import org.team401.vision.VisionDataStream.VisionData;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Turret implements Runnable {

    private TurretRotator turretRotator;
    private Switch trigger;
    private Switch magSensor;
    private VisionDataStream stream;
    private VisionData latestData;
    private double sentryState = 0;

    public Turret(VisionDataStream stream, CANTalon turretSpinner, CANTalon flyWheelMotor1, CANTalon flyWheelMotor2, CANTalon turretFeeder, Switch magSensor, Switch trigger) {
        this.stream = stream;
        turretRotator = new TurretRotator(turretSpinner, magSensor);
        latestData = new VisionData(0, 0, 0);
        this.magSensor = magSensor;
        this.trigger = trigger;
    }

    public void track() {
        if (!trigger.isTriggered())
            turretRotator.addDegrees(-latestData.getYaw());
        else
            turretRotator.stop();
    }

    public void sentryMode() {
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
                if (!trigger.isTriggered())
                    track();
                else
                    turretRotator.stop();
            } else
                sentryMode();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public TurretRotator getTurretRotator() {
        return turretRotator;
    }
}

