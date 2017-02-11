package org.team401.robot.commands;

import com.ctre.CANTalon;
import org.strongback.command.Command;
import org.team401.robot.components.Turret;
import org.team401.robot.components.TurretRotator;

/**
 * Created by Neema on 2/4/17.
 */
public class CalibrateTurret extends Command {

    private TurretRotator turretRotator;

    public CalibrateTurret(TurretRotator turretRotator) {
        this.turretRotator = turretRotator;
    }

    @Override
    public void initialize() {
        turretRotator.getRotator().changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        turretRotator.rotate(0.2);
    }

    @Override
    public boolean execute() {
        if (turretRotator.getZeroPoint().isTriggered())
            return true;
        return false;
    }

    @Override
    public void end() {
        turretRotator.rotate(0);
        turretRotator.getRotator().changeControlMode(CANTalon.TalonControlMode.Position);
        turretRotator.zero();
    }
}