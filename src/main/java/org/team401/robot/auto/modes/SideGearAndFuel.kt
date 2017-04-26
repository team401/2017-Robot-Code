package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.lib.VisionBuffer
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.AutoModeSelector
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.*

internal class SideGearAndFuel(startingPos: AutoModeSelector.StartingPos) : AutoMode() {

    val airshipAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 45.0 else -45.0
    val sideAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 105.0 else -105.0
    val intakeAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 10.0 else -10.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        GearHolder.setWantedState(GearHolder.GearHolderState.GEAR_VISION)

        runAction(DriveStraightAction(dStatToAir * 2, 12.0, Rotation2d.fromDegrees(0.0)))
        runAction(RotateAction(Rotation2d.fromDegrees(airshipAngle)))
        Thread.sleep(500)
        if (VisionBuffer.isLatestGearValid())
            runAction(DriveStraightAction(dAirToGear * 2, 5.0, Rotation2d.fromDegrees(airshipAngle + VisionBuffer.gearYaw())))
        else
            runAction(DriveStraightAction(dAirToGear * 2, 5.0, Rotation2d.fromDegrees(airshipAngle)))
        GearHolder.setWantedState(GearHolder.GearHolderState.CLOSED)

        runAction(DropGearAction(2.0))
        Thread.sleep(500)
        runAction(DriveStraightAction(dGearToBaseL*2, -10.0, Rotation2d.fromDegrees(airshipAngle)))

        runAction(RotateAction(Rotation2d.fromDegrees(sideAngle)))
        runAction(DriveStraightAction(dBaseLToHop * 2, -14.0, Rotation2d.fromDegrees(sideAngle)))
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        runAction(CalibrateTurretAction(Turret.TurretState.AUTO))

        Thread.sleep(1500)
        runAction(RotateAction(Rotation2d.fromDegrees(intakeAngle)))
    }

    override fun done() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}