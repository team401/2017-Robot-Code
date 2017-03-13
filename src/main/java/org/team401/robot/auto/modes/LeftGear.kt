package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class LeftGear : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        OctocanumDrive.setBrakeMode(true)
        val actions = mutableListOf(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-dStatToAir * 2, .8))
        runAction(ParallelAction(actions))
        runAction(RotateAction(Rotation2d.fromDegrees(55.0)))
        runAction(DriveDistanceAction(-dAirToGear * 2, 0.3))
        //TODO: alignment
        runAction(DropGearAction(3.0))
        runAction(WaitAction(1.5))
        runAction(DriveDistanceAction(dGearToBaseL * 2, .7))

        runAction(RotateAction(Rotation2d.fromDegrees(0.0)))
        runAction(DriveDistanceAction(-dBaseToReload * 2, .7))
    }

    override fun done() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        OctocanumDrive.setBrakeMode(false)
    }
}