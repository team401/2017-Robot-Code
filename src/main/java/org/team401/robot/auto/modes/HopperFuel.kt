package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.AutoModeSelector
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.auto.actions.DriveDistanceAction
import org.team401.robot.auto.actions.ParallelAction
import org.team401.robot.auto.actions.RotateAction
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.Intake
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

internal class HopperFuel(startingPos: AutoModeSelector.StartingPos) : AutoMode() {

    val turnAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 90.0 else -90.0
    val intakeAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 10.0 else -10.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.setBrakeMode(true)
        runAction(ParallelAction(CalibrateTurretAction(Turret.TurretState.AUTO), DriveDistanceAction(-7.0*12*2, .8, Rotation2d.fromDegrees(0.0))))

        runAction(RotateAction(Rotation2d.fromDegrees(turnAngle), 0.45))

        runAction(DriveDistanceAction(5.75 * 12 * 2, .6, Rotation2d.fromDegrees(turnAngle)))
        Turret.setWantedState(Turret.TurretState.AUTO)
        Thread.sleep(2500)
        runAction(RotateAction(Rotation2d.fromDegrees(intakeAngle)))
        Intake.setWantedState(Intake.IntakeState.ENABLED)
        runAction(DriveDistanceAction(3.5*2, .35, Rotation2d.fromDegrees(intakeAngle)))
    }

    override fun done() {
        Intake.setWantedState(Intake.IntakeState.DISABLED)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        OctocanumDrive.setBrakeMode(false)
    }
}