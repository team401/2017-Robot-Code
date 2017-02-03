package org.team401.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.Switch;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.vision.VisionDataStream.VisionData;
import org.team401.vision.VisionDataStream.VisionDataStream;

public class Robot extends IterativeRobot {

    private Motor turretSpinner;
    private FlightStick joysticky;
    private Switch switchy;
    private double sentryState = 1;
    private VisionDataStream visionDataStream;
    private VisionData currentVisionData;
    private double allowedRange;
    private double turningState;


    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);
        turretSpinner = Hardware.Motors.talonSRX(0);
        SmartDashboard.putNumber("allowedRange", 5);
        allowedRange = SmartDashboard.getNumber("allowedRange", 5);
        joysticky = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
        switchy = Hardware.Switches.normallyClosed(0);
        visionDataStream = new VisionDataStream("10.4.1.17", 5801);
        visionDataStream.start();
        turningState = 1;

    }

    @Override
    public void autonomousInit() {
        Strongback.start();
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        Strongback.restart();
    }

    @Override
    public void teleopPeriodic() {
        allowedRange = SmartDashboard.getNumber("allowedRange", 5);
        //vision variable
        currentVisionData = visionDataStream.getLatestData();

        //If the camera CAN see the target, it will rotate towards it and focus on it
        if (currentVisionData.isValid()) { //insert currentVisionData.isValid
            //Turns the turret to be facing the goal with about 5 degrees accuracy
            double correctionAngle = currentVisionData.getYaw() * -1;
            if (correctionAngle > allowedRange || correctionAngle < -allowedRange) {
                //enforces a soft stop so that the wires don't get tangled
                if (switchy.isTriggered() && correctionAngle < 0 && turningState < 1) {
                    turretSpinner.setSpeed(0);
                } else if (switchy.isTriggered() && correctionAngle > 0 && turningState > 1) {
                    turretSpinner.setSpeed(0);
                } else {
                    turretSpinner.setSpeed(correctionAngle/90 * 1.0);
                }
            } else {
                turretSpinner.setSpeed(0);
            }
            if (correctionAngle > 1)
                turningState = 1;
            else
                turningState = -1;
        }
        //If the cameron CANNOT see the target, it will go into sentry mode and scan to/fro until it finds the target
        else {
            turretSpinner.setSpeed(sentryState * 0.1);
            if (switchy.isTriggered() && sentryState > 1) {
                sentryState = -1;
            } else if (switchy.isTriggered() && sentryState <= 1) {
                sentryState = 1;
            }
        }
        //If the joystick is triggered (to shoot on the real robot) the turret won't spin
        if (joysticky.getTrigger().isTriggered()) {
            turretSpinner.setSpeed(0);
        }

    }

    @Override
    public void disabledInit() {Strongback.disable();}

    @Override
    public void disabledPeriodic() {}
}