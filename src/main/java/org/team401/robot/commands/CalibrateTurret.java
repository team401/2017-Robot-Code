package org.team401.robot.commands;

import com.ctre.CANTalon;
import org.strongback.command.Command;
import org.team401.robot.components.Turret;
import org.team401.robot.components.TurretRotator;

public class CalibrateTurret extends Command {

    private TurretRotator turretRotator;

    public CalibrateTurret(TurretRotator turretRotator) {
        this.turretRotator = turretRotator;
    }

    @Override
    public void initialize() {
        turretRotator.rotate(0.8);
    }

    @Override
    public boolean execute() {
        return turretRotator.getZeroPoint().isTriggered();
    }

    @Override
    public void end() {
        turretRotator.rotate(0);
        turretRotator.zero();
    }
}