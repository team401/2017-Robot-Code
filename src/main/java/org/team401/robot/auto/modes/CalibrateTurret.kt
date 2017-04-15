package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.auto.actions.DriveDistanceAction
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

internal class CalibrateTurret : AutoMode() {

    override fun routine() {
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        runAction(CalibrateTurretAction(Turret.TurretState.MANUAL))
    }
}