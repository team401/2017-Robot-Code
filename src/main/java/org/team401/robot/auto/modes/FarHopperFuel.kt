package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.AutoModeSelector
import org.team401.robot.auto.actions.DriveStraightAction
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Tower

internal class FarHopperFuel(startingPos: AutoModeSelector.StartingPos, val far: Boolean) : AutoMode() {

    val turnAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 90.0 else -90.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        val distance = if (!far) 131.0 else 151.0
        runAction(DriveStraightAction(distance * 2, 12.0, Rotation2d.fromDegrees(0.0)))
    }
}