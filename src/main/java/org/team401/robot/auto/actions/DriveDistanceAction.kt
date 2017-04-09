package org.team401.robot.auto.actions

import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.OctocanumDrive

class DriveDistanceAction @JvmOverloads constructor(val distance: Double, val power: Double = 0.5, val heading: Rotation2d = OctocanumDrive.getGyroAngle(), timeout: Double = 5.0) : Action(5.0) {

	var startPosLeft = 0.0
	var startPosRight = 0.0
	val brakeMode = OctocanumDrive.brakeModeOn

	override fun onStart() {
		startPosLeft = OctocanumDrive.getLeftDistanceInches()
		startPosRight = OctocanumDrive.getRightDistanceInches()
		OctocanumDrive.setBrakeMode(false)

		if (distance > 0)
			OctocanumDrive.drive(power, power)
		else
			OctocanumDrive.drive(-power, -power)
	}

	override fun onUpdate() {
		val angle = heading.inverse().rotateBy(OctocanumDrive.getGyroAngle()).degrees
		if (distance > 0)
			OctocanumDrive.drive(power - angle*.015, power + angle*.015)
		else
			OctocanumDrive.drive(-(power + angle*.015), -(power - angle*.015))
	}

	override fun isFinished(): Boolean {
		return Math.abs(OctocanumDrive.getLeftDistanceInches() - startPosLeft) > Math.abs(distance) &&
				Math.abs(OctocanumDrive.getRightDistanceInches() - startPosRight) > Math.abs(distance)
	}

	override fun onInterrupt() {
		onStop()
		println("Couldn't reach distance or encoders are bad!")
	}

	override fun onStop() {
		OctocanumDrive.setBrakeMode(brakeMode)
		OctocanumDrive.drive(0.0, 0.0)
	}
}