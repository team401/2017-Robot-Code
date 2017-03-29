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
    val sideAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 90.0 else -90.0
    val intakeAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 10.0 else -10.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        OctocanumDrive.setBrakeMode(true)
        runAction(ParallelAction(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-dStatToAir * 2, Rotation2d.fromDegrees(0.0), .8)))
        runAction(RotateAction(Rotation2d.fromDegrees(airshipAngle)))
        runAction(DriveDistanceAction(-dAirToGear * 2, Rotation2d.fromDegrees(airshipAngle), 0.3))
        //TODO: alignment
        runAction(DropGearAction(2.5))
        Thread.sleep(1500)
        runAction(DriveDistanceAction(dGearToBaseL * 2, Rotation2d.fromDegrees(airshipAngle), .7))

        runAction(RotateAction(Rotation2d.fromDegrees(sideAngle)))
        runAction(DriveDistanceAction(dBaseLToHop * 2, Rotation2d.fromDegrees(sideAngle), .7))
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        Turret.setWantedState(Turret.TurretState.AUTO)

        Thread.sleep(2000)
        runAction(RotateAction(Rotation2d.fromDegrees(intakeAngle)))

        Intake.setWantedState(Intake.IntakeState.ENABLED)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }

    override fun done() {
        Intake.setWantedState(Intake.IntakeState.DISABLED)
        OctocanumDrive.setBrakeMode(false)
    }
}