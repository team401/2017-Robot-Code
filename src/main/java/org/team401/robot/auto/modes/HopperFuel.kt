package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.AutoModeSelector
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.auto.actions.DriveStraightAction
import org.team401.robot.auto.actions.ParallelAction
import org.team401.robot.auto.actions.RotateAction
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.Intake
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

internal class HopperFuel(startingPos: AutoModeSelector.StartingPos, val far: Boolean) : AutoMode() {

    val turnAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 90.0 else -90.0
    val intakeAngle = if (startingPos == AutoModeSelector.StartingPos.LEFT) 115.0 else -115.0

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)

        val distance = if (!far) dStatToAir+6 else dStatToAir+26
        runAction(DriveStraightAction(distance * 2, 12.0, Rotation2d.fromDegrees(0.0), far))

        runAction(RotateAction(Rotation2d.fromDegrees(turnAngle), 0.45))

        runAction(DriveStraightAction(dBaseLToHop * 2, -14.0, Rotation2d.fromDegrees(turnAngle)))
        Intake.setWantedState(Intake.IntakeState.ARM_DOWN)
        runAction(CalibrateTurretAction(Turret.TurretState.AUTO))
        Thread.sleep(1500)
        Intake.setWantedState(Intake.IntakeState.DISABLED)
        runAction(RotateAction(Rotation2d.fromDegrees(intakeAngle)))
    }

    override fun done() {
        Intake.setWantedState(Intake.IntakeState.DISABLED)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}