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
        OctocanumDrive.setBrakeMode(true)
        runAction(ParallelAction(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-3.5*12*2, 0.9, Rotation2d.fromDegrees(0.0))))
        runAction(DriveDistanceAction(-1.75*12*2, 0.4, Rotation2d.fromDegrees(0.0)))
        runAction(DropGearAction(2.5))
        Thread.sleep(1000)
        runAction(DriveDistanceAction(4.5*12*2, 0.4, Rotation2d.fromDegrees(0.0)))

        runAction(RotateAction(Rotation2d.fromDegrees(turnAngle)))
        runAction(DriveDistanceAction(3.0*12*2, 0.4, Rotation2d.fromDegrees(turnAngle)))

        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        Turret.setWantedState(Turret.TurretState.AUTO)
    }

    override fun done() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}