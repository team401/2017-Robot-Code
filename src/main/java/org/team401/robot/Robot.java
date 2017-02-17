package org.team401.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.commands.ShiftDriveMode;
import org.team401.robot.components.CollectionGearbox;
import org.team401.robot.components.OctocanumGearbox;

public class Robot extends IterativeRobot {

    private Motor turretSpinner;
    private FlightStick joysticky;
    private Switch switchy;
    private double sentryState = 1;
    private VisionDataStream visionDataStream;
    private Turret turret;

    private Thread turretThread;


    @Override
    public void robotInit() {
        Strongback.configure()
                .recordDataToFile("/home/lvuser/")
                .recordEventsToFile("/home/lvuser/", 2097152);
        turretSpinner = Hardware.Motors.talonSRX(0);
        SmartDashboard.putNumber("allowedRange", 5);
        joysticky = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
        switchy = Hardware.Switches.normallyClosed(0);
        visionDataStream = new VisionDataStream("10.4.1.17", 5801);
        visionDataStream.start();

        OctocanumGearbox frontLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT));
        OctocanumGearbox frontRight = new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT));
        OctocanumGearbox rearLeft = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT));
        OctocanumGearbox rearRight = new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT));
        ADIS16448_IMU g = new ADIS16448_IMU();
        g.calibrate();
        octocanumDrive = new OctocanumDrive(frontLeft, frontRight, rearLeft, rearRight, new Solenoid(Constants.GEARBOX_SHIFTER), g);

        CollectionGearbox collectionGearbox = new CollectionGearbox(
                Hardware.Motors.victorSP(Constants.COL_PRO_1),
                Hardware.Motors.victorSP(Constants.COL_PRO_2),
                Hardware.Motors.victorSP(Constants.COL_PRO_3)
        );

        driveJoystickLeft = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT);
        driveJoystickRight = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT);
        masherJoystick = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK);
        turret = new Turret(visionDataStream, new CANTalon(0), new CANTalon(1),
                new CANTalon(2), new CANTalon(3), turretHood,
                Hardware.Switches.normallyClosed(Constants.TURRET_LIMIT_SWITCH),
                joysticky.getTrigger(), joysticky.getYaw());
        turretThread = new Thread(turret);
        turretThread.start();

        camera = new Camera(640, 480, 10);

        Solenoid collectionExtender = new Solenoid(Constants.COL_EXTENDER);
        collectionExtender.set(false);
        Solenoid turretExtender = new Solenoid(Constants.TURRET_SHIFTER);
        turretExtender.set(false);

        SwitchReactor switchReactor = Strongback.switchReactor();

        // shift drive modes
        switchReactor.onTriggeredSubmit(driveJoystickLeft.getTrigger(),
                () -> new ShiftDriveMode(octocanumDrive));
        // camera switching
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_SWITCH_CAMERA),
                () -> camera.switchCamera());
        // collection
        switchReactor.onTriggered(driveJoystickRight.getButton(Constants.BUTTON_COL_DROP),
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
        // turret
        switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_EXTEND_TURRET),
                () -> {
                    turretExtender.set(!turretExtender.get());
                    SmartDashboard.putBoolean("Turret Extended", turretExtender.get());
                });
        //switchReactor.onTriggered(masherJoystick.getButton(Constants.BUTTON_TOGGLE_HOOD));
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
        Strongback.submit(new CalibrateTurret(turret.getTurretRotator()));
    }

    @Override
    public void teleopPeriodic() {
        turret.run();
    }

    @Override
    public void disabledInit() {Strongback.disable();}

    @Override
    public void disabledPeriodic() {

    }
}