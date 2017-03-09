package org.team401.robot.auto.modes

import edu.wpi.first.wpilibj.Timer
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class RightGearMode : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        val actions = mutableListOf(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-4.0*12))
        runAction(ParallelAction(actions))
        runAction(RotateAction(Rotation2d.fromDegrees(-45.0)))
        runAction(DriveDistanceAction(-2.0*12, 0.3))
        //TODO: alignment
        runAction(DropGearAction(3.0))
        Timer.delay(2.0)
        runAction(DriveDistanceAction(3.0*12))
    }
}