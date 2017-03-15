package org.team401.robot.auto.modes

import org.team401.lib.Rotation2d
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.Intake
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class RightGearAndFuel : AutoMode() {

	override fun routine() {
		OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
		GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
		OctocanumDrive.setBrakeMode(true)
		runAction(ParallelAction(mutableListOf(
			CalibrateTurretAction(Turret.TurretState.AUTO),
			DriveDistanceAction(-dStatToAir * 2, .8))))
		runAction(RotateAction(Rotation2d.fromDegrees(-55.0)))
		runAction(DriveDistanceAction(-dAirToGear * 2, 0.3))
		//TODO: alignment
		runAction(DropGearAction(3.0))
		Thread.sleep(1500)
		runAction(DriveDistanceAction(dGearToBaseL * 2, .7))

		runAction(RotateAction(Rotation2d.fromDegrees(-90.0)))
		runAction(DriveDistanceAction(dBaseLToHop * 2, .7))

		Thread.sleep(2000)
		runAction(RotateAction(Rotation2d.fromDegrees(-70.0)))

		Intake.enabled = true
		OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
	}

	override fun done() {
		Intake.enabled = false
		OctocanumDrive.setBrakeMode(false)
	}
}