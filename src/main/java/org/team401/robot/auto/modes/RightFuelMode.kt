package org.team401.robot.auto.modes

import edu.wpi.first.wpilibj.Timer
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.auto.actions.DriveDistanceAction
import org.team401.robot.auto.actions.ParallelAction
import org.team401.robot.auto.actions.RotateAction
import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class RightFuelMode : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        val actions = mutableListOf(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(4.0*12))
        runAction(ParallelAction(actions))
        runAction(RotateAction(Rotation2d.fromDegrees(90.0)))
        runAction(DriveDistanceAction(3.0*12))
        Timer.delay(4.0)
        runAction(RotateAction(Rotation2d.fromDegrees(20.0)))
    }
}