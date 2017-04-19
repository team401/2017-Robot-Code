package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

internal class CenterGear : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        // drive up slowly
        runAction(DriveStraightAction(3.5*2, 14.0, Rotation2d.fromDegrees(0.0), true))
        runAction(DriveStraightAction(1.75*2, 5.0, Rotation2d.fromDegrees(0.0)))
        runAction(DropGearAction(2.0))
        Thread.sleep(1000)
        runAction(DriveStraightAction(3.5*2, -8.0, Rotation2d.fromDegrees(0.0)))

        runAction(CalibrateTurretAction(Turret.TurretState.MANUAL))
    }

    override fun done() {
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}