package org.team401.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.strongback.components.ui.FlightStick;
import org.strongback.hardware.Hardware;
import org.team401.robot.auto.AutoModeExecuter;
import org.team401.robot.auto.modes.AutoTestMode;
import org.team401.robot.auto.modes.CalibrateTurretMode;
import org.team401.robot.components.TurretRotator;
import org.team401.robot.subsystems.OctocanumDrive;
import org.team401.robot.loops.LoopManager;

public class RobotTestPls extends IterativeRobot {

    private TurretRotator turret;
    private CANTalon feeder;

    private FlightStick driveJoystickLeft, driveJoystickRight, masherJoystick;

    //@Override
    public void robotInit() {
        turret = new TurretRotator(new CANTalon(Constants.TURRET_ROTATOR));
        feeder = new CANTalon(Constants.TURRET_FEEDER);

    }

    //@Override
    public void autonomousInit() {
        new AutoModeExecuter(new CalibrateTurretMode()).start();
    }

    //@Override
    public void teleopInit() {
        feeder.enableLimitSwitch(true, true);
    }

    //@Override
    public void disabledInit() {

    }

    //@Override
    public void autonomousPeriodic() {

    }

    //@Override
    public void teleopPeriodic() {
        turret.setPosition(SmartDashboard.getNumber("pos", 0.0));
        SmartDashboard.putNumber("turret_position", turret.getPosition());
        SmartDashboard.putNumber("turret_error", turret.getError());

        SmartDashboard.putBoolean("fwd_limit", feeder.isFwdLimitSwitchClosed());
        SmartDashboard.putBoolean("rev_limit", feeder.isRevLimitSwitchClosed());
    }
}
