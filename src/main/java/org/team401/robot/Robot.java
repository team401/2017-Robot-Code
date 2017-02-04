/**
 * FRC Team 401 2017 Autonomous Testing
 */

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


public class Robot extends IterativeRobot {
	private SendableChooser autoStart, autoTgt;
	private Auto2017 autonomous;
	private OctocanumDrive drive;

	private FlightStick joy0, joy1;

	@Override
	public void robotInit() {
		joy0 = Hardware.HumanInterfaceDevices.logitechAttack3D(0);
		joy1 = Hardware.HumanInterfaceDevices.logitechAttack3D(1);

		//init drive
		drive = new OctocanumDrive(
				new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_LEFT), new CANTalon(Constants.PRO_FRONT_LEFT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_FRONT_RIGHT), new CANTalon(Constants.PRO_FRONT_RIGHT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_LEFT), new CANTalon(Constants.PRO_REAR_LEFT)),
				new OctocanumGearbox(new CANTalon(Constants.CIM_REAR_RIGHT), new CANTalon(Constants.PRO_REAR_RIGHT)),
				new Solenoid(Constants.GEARBOX_SHIFTER));

		SmartDashboard.putString("", "DO NOT SELECT STARTING POSITIONS AND HOPPERS OF OPPOSITE DIRECTIONS!!!");
		//creates radio buttons for selecting the robots path
		autoStart = new SendableChooser();
		autoStart.addDefault("Center", "C");
		autoStart.addObject("Left", "L");
		autoStart.addObject("Right", "R");
		SmartDashboard.putData("Starting Position", autoStart);

		autoTgt = new SendableChooser();
		autoTgt.addDefault("Center Lift", "CL");
		autoTgt.addObject("Left Lift", "LL");
		autoTgt.addObject("Right Lift", "RL");
		autoTgt.addObject("Left Hopper", "LH");
		autoTgt.addObject("Right Hopper", "RH");


		SmartDashboard.putData("Auto Destination", autoTgt);

		SmartDashboard.putBoolean("Mecanum Drive", true);

	}

	@Override
	public void autonomousInit() {
		for (OctocanumGearbox x:drive.getGearboxes()) {
			x.setControlMode(CANTalon.TalonControlMode.MotionProfile);
		}
		autonomous = new Auto2017((String)autoStart.getSelected(),
			(String)autoTgt.getSelected(),
			SmartDashboard.getBoolean("Mecanum Drive", true),
			drive);
	}


	@Override
	public void autonomousPeriodic(){
		autonomous.periodic();
	}

	@Override
	public void teleopPeriodic() {
		if(joy0.getTrigger().isTriggered()||joy1.getTrigger().isTriggered())
			drive.shift();
		drive.drive(joy0.getPitch().read(), joy0.getRoll().read(), joy1.getPitch().read(), joy1.getRoll().read());
	}
	@Override
	public void disabledPeriodic() {
		drive.drive(0, 0, 0, 0);
	}

}
