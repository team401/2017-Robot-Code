package org.team401.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.Strongback;
import org.strongback.SwitchReactor;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;

import org.team401.robot.camera.Camera;
import org.team401.robot.chassis.OctocanumDrive;
import org.team401.robot.commands.ToggleDriveMode;
import org.team401.robot.components.OctocanumGearbox;

/**
 * FRC Team 401 2017 Autonomous Test Project
 * Auto: Select from 28 possible autonomous modes.  These are stored in .csv files which should be uploaded to the robot's filesystem.
 * Teleop: Drive with 2 joysticks.  The encoders' velocities are sent to SmartDashboard for F-Gain calculation.
 */

public class Robot extends IterativeRobot {
	//SendableChoosers allow selection from a list at runtime
	private SendableChooser autoStart, autoTgt;

	//Autonomous mode is abstracted with another class
	private Auto2017 autonomous;

	private OctocanumDrive drive;
	private FlightStick joy0, joy1;

	@Override
	public void robotInit() {//Called on startup
		//Joysticks
		joy0 = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
		joy1 = Hardware.HumanInterfaceDevices.logitechAttack3D(1);

		//init drive
		drive = new OctocanumDrive(
				new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT)),
				new Solenoid(Constants.GEARBOX_SHIFTER),
				new ADIS16448_IMU());

		//Switch to tank drive default
		drive.shift(OctocanumDrive.DriveMode.TRACTION);

		//Reminder that a couple options aren't planned for and won't do anything
		SmartDashboard.putString("", "DO NOT SELECT STARTING POSITIONS AND HOPPERS OF OPPOSITE DIRECTIONS!!!");

		//Create radio buttons for selecting the robot's starting position
		autoStart = new SendableChooser();
		autoStart.addDefault("Center", "C");
		autoStart.addObject("Left", "L");
		autoStart.addObject("Right", "R");
		SmartDashboard.putData("Starting Position", autoStart);

		//defaults in traction mode until all encoders are done
		drive.setDriveMode(OctocanumDrive.DriveMode.TRACTION);

		SwitchReactor switchReactor = Strongback.switchReactor();

		Camera camera = new Camera(640, 480, 10);

        // shift drive modes
        switchReactor.onTriggeredSubmit(joy0.getTrigger(),
                () -> new ToggleDriveMode(drive));
        // camera switching
        switchReactor.onTriggered(joy1.getButton(Constants.BUTTON_SWITCH_CAMERA),
                () -> camera.switchCamera());
        switchReactor.onTriggered(joy1.getButton(2), () -> {
            drive.getGyro().reset();
            System.out.println("calibrated");
        });
        switchReactor.onTriggered(joy0.getButton(4), () -> {
            SmartDashboard.putBoolean("Gyro Enabled", !SmartDashboard.getBoolean("Gyro Enabled", true));
            System.out.println(SmartDashboard.getBoolean("Gyro Enabled", true));
        });
		//Reminder that a couple options aren't planned for and won't do anything
		SmartDashboard.putString("", "DO NOT SELECT STARTING POSITIONS AND HOPPERS OF OPPOSITE DIRECTIONS!!!");

		//Create radio buttons for selecting the robot's starting position
		autoStart = new SendableChooser();
		autoStart.addDefault("Center", "C");
		autoStart.addObject("Left", "L");
		autoStart.addObject("Right", "R");
		SmartDashboard.putData("Starting Position", autoStart);

		//Create radio buttons for selecting the robot's destination
		autoTgt = new SendableChooser();
		autoTgt.addDefault("Center Lift", "CL");
		autoTgt.addObject("Left Lift", "LL");
		autoTgt.addObject("Right Lift", "RL");
		autoTgt.addObject("Left Hopper", "LH");
		autoTgt.addObject("Right Hopper", "RH");
		SmartDashboard.putData("Auto Destination", autoTgt);

		//Button to select starting drive mode
		SmartDashboard.putBoolean("Mecanum Drive", false);
	}

	@Override
	public void autonomousInit() {//Called on match start
		//Start autonomous, passing through data from here
		autonomous = new Auto2017((String)autoStart.getSelected(),
			(String)autoTgt.getSelected(),
			SmartDashboard.getBoolean("Mecanum Drive", false),
			drive);
	}

	@Override
	public void autonomousPeriodic(){//Called every 20ms during first 15s of match
		//See Auto2017.java
		autonomous.periodic();
	}
	@Override
	public void teleopInit(){
		//reset Talon control modes
		for (OctocanumGearbox x: drive.getGearboxes())
			x.setControlMode(CANTalon.TalonControlMode.PercentVbus);

		//reset Strongback/gyro
		Strongback.restart();
		drive.getGyro().reset();
	}

	@Override
	public void teleopPeriodic() {//Called every 20ms from 15s to end of match
		//Drive according to joysticks
		drive.drive(joy0.getPitch().read(), joy0.getRoll().read(), joy1.getPitch().read(), joy1.getRoll().read());


		//Get encoder data
		double fls = drive.getGearboxes().get(0).getCimMotor().getEncVelocity(),
				frs = drive.getGearboxes().get(1).getCimMotor().getEncVelocity(),
				rls = drive.getGearboxes().get(2).getCimMotor().getEncVelocity(),
				rrs = drive.getGearboxes().get(3).getCimMotor().getEncVelocity();

		//Send encoder data to SD to manually calculate F-Gain if desired
		SmartDashboard.putNumber("Front Left Speed", fls);
		SmartDashboard.putNumber("Front Right Speed", frs);
		SmartDashboard.putNumber("Rear Left Speed", rls);
		SmartDashboard.putNumber("Rear Right Speed", rrs);
	}


	@Override
	public void disabledPeriodic() {//Called when the robot is on but inactive
		drive.drive(0, 0, 0, 0);
	}

	//percentNeeded defaults to 100
	private static double fGain(double maxSpeed){
		return fGain(maxSpeed, 100);
	}

	/**
	 * Calculates the Talons' F-gain based on Page 85 of:
	 * http://www.ctr-electronics.com/downloads/pdf/Talon%20SRX%20Software%20Reference%20Manual.pdf
	 *
	 * @param maxSpeed Maximum speed reachable by the Talon's output, in encoder units/100ms.
	 * @param percentNeeded 1 to 100, inclusive.  This should be the percentage of power the Talon needs to reach maxSpeed.
	 * @return Correct F-gain to send to the Talon for motion profiling
	 */
	private static double fGain(double maxSpeed, double percentNeeded){
		return percentNeeded / 100 * 1023 / maxSpeed;
	}
}