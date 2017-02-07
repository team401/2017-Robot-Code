package org.team401.robot;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;

import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;

import org.team401.robot.chassis.OctocanumDrive;
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
				new Solenoid(Constants.GEARBOX_SHIFTER));

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
		SmartDashboard.putBoolean("Mecanum Drive", true);
	}

	@Override
	public void autonomousInit() {//Called on match start
		//Start autonomous, passing through data from here
		autonomous = new Auto2017((String)autoStart.getSelected(),
			(String)autoTgt.getSelected(),
			SmartDashboard.getBoolean("Mecanum Drive", true),
			drive);
	}

	@Override
	public void autonomousPeriodic(){//Called every 20ms during first 15s of match
		//See Auto2017.java
		autonomous.periodic();
	}

	@Override
	public void teleopPeriodic() {//Called every 20ms from 15s to end of match
		//Drive according to joysticks
		drive.drive(joy0.getPitch().read(), joy0.getRoll().read(), joy1.getPitch().read(), joy1.getRoll().read());

		//Send encoder data to SD to calculate F-Gain
		SmartDashboard.putNumber("Front Left Speed", drive.getGearboxes().get(0).getCimMotor().getEncVelocity());
		SmartDashboard.putNumber("Front Right Speed", drive.getGearboxes().get(1).getCimMotor().getEncVelocity());
		SmartDashboard.putNumber("Rear Left Speed", drive.getGearboxes().get(2).getCimMotor().getEncVelocity());
		SmartDashboard.putNumber("Rear Right Speed", drive.getGearboxes().get(3).getCimMotor().getEncVelocity());



		//(100% * 1023)/max speed in native units per 100ms = F gain
	}

	@Override
	public void disabledPeriodic() {//Called when the robot is on but inactive
		drive.drive(0, 0, 0, 0);
	}

	public static double FGain(double maxSpeed){
		return 1023/maxSpeed;
	}


}