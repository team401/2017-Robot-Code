package org.team401.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.components.OctocanumGearbox;
import org.team401.robot.commands.ToggleDriveMode;
import org.team401.robot.sensors.DistanceSensor;
import org.team401.robot.sensors.SHARPGP2I2;

public class Robot extends IterativeRobot {

    private OctocanumDrive octocanumDrive;
    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private Camera camera;

    private DistanceSensor sensor;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        sensor = new SHARPGP2I2(0);
    }

    @Override
    public void robotPeriodic() {

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
        // drive the robot, mode specific drive code is in the OctocanumDrive class
        System.out.println(sensor.getDistance() + " cm");
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
    }

    @Override
    public void disabledPeriodic() {}
}
