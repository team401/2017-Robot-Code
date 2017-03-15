package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.auto.actions.DriveDistanceAction
import org.team401.robot.auto.actions.ParallelAction
import org.team401.robot.auto.actions.RotateAction
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.Intake
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class LeftFuel : AutoMode() {

	override fun routine() {
		OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
		GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
		OctocanumDrive.setBrakeMode(true)
		runAction(ParallelAction(mutableListOf(
			CalibrateTurretAction(Turret.TurretState.AUTO),
			DriveDistanceAction(dStatToAir*2, .8))))

		runAction(RotateAction(Rotation2d.fromDegrees(90.0), 0.45))

		runAction(DriveDistanceAction(dBaseLToHop * 2, .6))
		Thread.sleep(2000)
		runAction(RotateAction(Rotation2d.fromDegrees(10.0)))
		Intake.enabled = true
		runAction(DriveDistanceAction(3.5, .35))
	}

	override fun done() {
		Intake.enabled = false
		OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
		OctocanumDrive.setBrakeMode(false)
	}
}