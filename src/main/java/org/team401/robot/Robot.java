package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.commands.ShiftDriveMode;
import org.team401.robot.components.OctocanumGearbox;

public class Robot extends IterativeRobot {

    private OctocanumDrive octocanumDrive;
    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;
    private Camera camera;

    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);

        // turn compressor fan on
        new Solenoid(Constants.COMPRESSOR_FAN).set(true);

        OctocanumGearbox frontLeft = new OctocanumGearbox(new CANTalon(Constants.FRONT_LEFT_MASTER), new CANTalon(Constants.FRONT_LEFT_SLAVE));
        OctocanumGearbox frontRight = new OctocanumGearbox(new CANTalon(Constants.FRONT_RIGHT_MASTER), new CANTalon(Constants.FRONT_RIGHT_SLAVE));
        OctocanumGearbox rearLeft = new OctocanumGearbox(new CANTalon(Constants.REAR_LEFT_MASTER), new CANTalon(Constants.REAR_LEFT_SLAVE));
        OctocanumGearbox rearRight = new OctocanumGearbox(new CANTalon(Constants.REAR_RIGHT_MASTER), new CANTalon(Constants.REAR_RIGHT_SLAVE));
        ADXRS450_Gyro g = new ADXRS450_Gyro();
        g.calibrate();
        octocanumDrive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, new Solenoid(Constants.GEARBOX_SHIFTER), g);

        /*CollectionGearbox collectionGearbox = new CollectionGearbox(
                Hardware.Motors.victorSP(Constants.COL_PRO_1),
                Hardware.Motors.victorSP(Constants.COL_PRO_2),
                Hardware.Motors.victorSP(Constants.COL_PRO_3)
        );*/

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);

        camera = new Camera(640, 480, 10);

        /*Solenoid collectionExtender = new Solenoid(Constants.COL_EXTENDER);
        collectionExtender.set(false);
        Solenoid turretExtender = new Solenoid(Constants.TURRET_SHIFTER);
        turretExtender.set(false);
        Solenoid gearScoring = new Solenoid(Constants.GEAR_HOLDER);*/

        SwitchReactor switchReactor = Strongback.switchReactor();

        // shift drive modes
        switchReactor.onTriggeredSubmit(driveJoystickLeft.getButton(Constants.BUTTON_SHIFT),
                () -> new ShiftDriveMode(octocanumDrive));
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_TOGGLE_GYRO),
                () -> SmartDashboard.putBoolean("Field-Centric", !SmartDashboard.getBoolean("Field-Centric", true)));
        // camera switching
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_SWITCH_CAMERA),
                () -> camera.switchCamera());
        // collection
        /*switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_COL_DROP),
                () -> {
                    collectionExtender.set(!collectionExtender.get());
                    SmartDashboard.putBoolean("Collection Down", collectionExtender.get());
                });
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_COL_TOGGLE),
                () -> {
                    if (collectionGearbox.isRunning())
                        collectionGearbox.setSpeed(0);
                    else
                        collectionGearbox.setSpeed(1);
                    SmartDashboard.putBoolean("Collection Enabled", collectionGearbox.isRunning());
                });
        // gears
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_GEAR),
                () -> {
                    gearScoring.set(true);
                    SmartDashboard.putBoolean("Gear Holder Open", true);
                });
        switchReactor.onUntriggered(driveJoystickRight.getButton(Constants.BUTTON_GEAR),
                () -> {
                    gearScoring.set(false);
                    SmartDashboard.putBoolean("Gear Holder Open", false);
                });
        // turret
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_EXTEND_TURRET),
                () -> {
                    turretExtender.set(!turretExtender.get());
                    SmartDashboard.putBoolean("Turret Extended", turretExtender.get());
                });
        //switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_HOOD));*/
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
                driveJoystickRight.getRoll().read());
    }

    @Override
    public void disabledInit() {
        Strongback.disable();
    }

    @Override
    public void disabledPeriodic() {}
}
