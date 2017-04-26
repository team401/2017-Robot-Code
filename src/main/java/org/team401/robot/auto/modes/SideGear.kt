package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.lib.Rotation2d
import org.team401.lib.VisionBuffer
import org.team401.robot.auto.AutoModeSelector
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

internal class SideGear(startingPos: AutoModeSelector.StartingPos) : AutoMode() {

    val airshipAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 50.0 else -50.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        runAction(DriveStraightAction(dStatToAir * 2, 12.0, Rotation2d.fromDegrees(0.0)))
        runAction(RotateAction(Rotation2d.fromDegrees(airshipAngle)))
        Thread.sleep(500)
        if (VisionBuffer.isLatestGearValid())
            runAction(DriveStraightAction(dAirToGear * 2, 5.0, Rotation2d.fromDegrees(airshipAngle + VisionBuffer.gearYaw())))
        else
            runAction(DriveStraightAction(dAirToGear * 2, 5.0, Rotation2d.fromDegrees(airshipAngle)))
        //TODO: alignment
        runAction(DropGearAction(2.0))
        Thread.sleep(1000)
        runAction(DriveStraightAction(dAirToGear* 2, -6.0, Rotation2d.fromDegrees(airshipAngle)))

        runAction(RotateAction(Rotation2d.fromDegrees(0.0)))
        runAction(DriveStraightAction(dBaseToReload * 2, 12.0, Rotation2d.fromDegrees(0.0)))

        runAction(CalibrateTurretAction(Turret.TurretState.MANUAL))
    }

    override fun done() {
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}