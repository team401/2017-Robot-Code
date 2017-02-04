package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.components.OctocanumGearbox;
import org.team401.robot.commands.ToggleDriveMode;

public class Robot extends IterativeRobot {

    private OctocanumDrive octocanumDrive;
    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private Camera camera;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        OctocanumGearbox frontLeft = new OctocanumGearbox(Hardware.Motors.victorSP(0), Hardware.Motors.victorSP(1));
        OctocanumGearbox frontRight = new OctocanumGearbox(Hardware.Motors.victorSP(4), Hardware.Motors.victorSP(5));
        OctocanumGearbox rearLeft = new OctocanumGearbox(Hardware.Motors.victorSP(2), Hardware.Motors.victorSP(3));
        OctocanumGearbox rearRight = new OctocanumGearbox(Hardware.Motors.victorSP(6), Hardware.Motors.victorSP(7));
        octocanumDrive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, new Solenoid(0));

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        //masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        camera = new Camera(640, 480, 10);

        SwitchReactor switchReactor = Strongback.switchReactor();

        // shift drive modes
        switchReactor.onTriggeredSubmit(driveJoystickLeft.getTrigger(),
                () -> new ToggleDriveMode(octocanumDrive));
        // camera switching
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_SWITCH_CAMERA),
                () -> camera.switchCamera());
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
        octocanumDrive.drive(driveJoystickLeft.getPitch().read(), driveJoystickLeft.getRoll().read(),
                driveJoystickRight.getPitch().read(), driveJoystickRight.getRoll().read());
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
    }

    @Override
    public void disabledPeriodic() {}
}
