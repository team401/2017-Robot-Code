package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.AutoModeSelector
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.Intake
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

internal class SideGearAndFuel(startingPos: AutoModeSelector.StartingPos) : AutoMode() {

    val airshipAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 50.0 else -50.0
    val sideAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 105.0 else -105.0
    val intakeAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 10.0 else -10.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        runAction(ParallelAction(CalibrateTurretAction(Turret.TurretState.SENTRY),
                DriveDistanceAction(dStatToAir * 2, 12.0, Rotation2d.fromDegrees(0.0))))
        runAction(RotateAction(Rotation2d.fromDegrees(airshipAngle)))
        runAction(DriveDistanceAction(dAirToGear * 2, 5.0, Rotation2d.fromDegrees(airshipAngle)))
        //TODO: alignment
        //
        runAction(DropGearAction(2.0))
        Thread.sleep(1000)
        runAction(DriveDistanceAction(dGearToBaseL*2, -10.0, Rotation2d.fromDegrees(airshipAngle)))

        runAction(RotateAction(Rotation2d.fromDegrees(sideAngle)))
        runAction(DriveDistanceAction(dBaseLToHop * 2, -14.0, Rotation2d.fromDegrees(sideAngle)))
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        Turret.setWantedState(Turret.TurretState.AUTO)

        Thread.sleep(2000)
        runAction(RotateAction(Rotation2d.fromDegrees(intakeAngle)))

        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }

    override fun done() {

    }
}