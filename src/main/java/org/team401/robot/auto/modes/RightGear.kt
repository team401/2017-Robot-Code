package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class RightGear : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        OctocanumDrive.setBrakeMode(true)
        val actions = mutableListOf(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-dStatToAir * 2, .8))
        runAction(ParallelAction(actions))
        runAction(RotateAction(Rotation2d.fromDegrees(-50.0)))
        runAction(DriveDistanceAction(-dAirToGear * 2, 0.3))
        //TODO: alignment
        //runAction(DropGearAction(3.0))
        Thread.sleep(1000)
        runAction(DriveDistanceAction(dGearToBaseL * 2, .5))

        /*runAction(RotateAction(Rotation2d.fromDegrees(0.0)))
        runAction(DriveDistanceAction(-dBaseToReload * 2, .7))*/
    }

    override fun done() {
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        OctocanumDrive.setBrakeMode(false)
    }
}