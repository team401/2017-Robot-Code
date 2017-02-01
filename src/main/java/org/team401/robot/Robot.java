package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.Motor;
import org.strongback.components.Solenoid;
import org.strongback.components.Switch;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.Hopper;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.chassis.OctocanumGearbox;
import org.team401.robot.commands.ToggleDriveMode;

public class Robot extends IterativeRobot {

    private OctocanumDrive octocanumDrive;
    private Hopper hopper;
    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private Camera camera;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        OctocanumGearbox frontLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT));
        OctocanumGearbox frontRight = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT));
        OctocanumGearbox rearLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT));
        OctocanumGearbox rearRight = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT));
        Solenoid shifter = Hardware.Solenoids.doubleSolenoid(Constants.GEARBOX_SHIFTER,0, Solenoid.Direction.RETRACTING);
        octocanumDrive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, shifter);

        Motor intakeLeft = Hardware.Motors.talonSRX(Constants.INTAKE_1);
        Motor intakeRight = Hardware.Motors.talonSRX(Constants.INTAKE_2);
        Motor agitator = Hardware.Motors.talonSRX(Constants.HOPPER_AGITATOR);
        hopper = new Hopper(intakeLeft, intakeRight, agitator);

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        camera = new Camera(640, 480, 10);

        SwitchReactor switchReactor = Strongback.switchReactor();

        // shift drive modes
        switchReactor.onTriggered(driveJoystickLeft.getButton(Constants.BUTTON_SHIFT),
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
        /*hopper.enableIntake(true);
        hopper.enableAgitator(true);*/
    }

    @Override
    public void autonomousPeriodic() {

    }

    @Override
    public void teleopInit() {
        Strongback.restart();
        /*hopper.enableIntake(true);
        hopper.enableAgitator(true);*/
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
        /*hopper.enableIntake(false);
        hopper.enableAgitator(false);*/
    }

    @Override
    public void disabledPeriodic() {}
}
