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
        runAction(ParallelAction(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-3.5*12*2, 0.9, Rotation2d.fromDegrees(0.0))))
        runAction(DriveDistanceAction(-1.75*12*2, 0.4, Rotation2d.fromDegrees(0.0)))
        runAction(DropGearAction(2.5))
        Thread.sleep(1000)
        runAction(DriveDistanceAction(4.5*12*2, .4, Rotation2d.fromDegrees(0.0)))
    }

    override fun done() {
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}