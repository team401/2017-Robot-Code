package org.team401.robot.auto.modes

import org.team401.lib.FMS
import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.Turret

class CenterGearAndFuel : AutoMode() {

    val turnAngle = if (FMS.getAlliance() == FMS.Alliance.RED) -90.0 else 90.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        DriveStraightAction(3.5*2, 14.0, Rotation2d.fromDegrees(0.0), true)
        runAction(DriveStraightAction(1.75*2, 7.0, Rotation2d.fromDegrees(0.0)))
        runAction(DropGearAction(2.0))
        Thread.sleep(1000)
        runAction(DriveStraightAction(4.5*2, -8.0, Rotation2d.fromDegrees(0.0)))

        runAction(RotateAction(Rotation2d.fromDegrees(turnAngle)))
        runAction(DriveStraightAction(3.0*2, -8.0, Rotation2d.fromDegrees(turnAngle)))

        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        runAction(CalibrateTurretAction(Turret.TurretState.AUTO))
    }

    override fun done() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}