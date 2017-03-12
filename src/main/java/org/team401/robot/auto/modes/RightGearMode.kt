package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class RightGearMode : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        OctocanumDrive.setBrakeMode(true)
        val actions = mutableListOf(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-4.75*12*2, .8))
        runAction(ParallelAction(actions))
        //runAction(DriveDistanceAction(-5.75*12))
        runAction(RotateAction(Rotation2d.fromDegrees(-55.0), .9))
        runAction(DriveDistanceAction(-2.75*12*2, 0.3))
        //TODO: alignment
        runAction(DropGearAction(3.0))
        runAction(WaitAction(1.5))
        runAction(DriveDistanceAction(3.25*12*2, .7))
        /*runAction(RotateAction(Rotation2d.fromDegrees(0.0)))
        runAction(DriveDistanceAction(-6.0*12))
        OctocanumDrive.setBrakeMode(false)*/
        runAction(RotateAction(Rotation2d.fromDegrees(-90.0), .9))
        runAction(DriveDistanceAction(2.5*12*2, .7))
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        OctocanumDrive.setBrakeMode(false)
    }
}