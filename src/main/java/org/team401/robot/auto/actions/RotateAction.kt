package org.team401.robot.auto.actions

import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.OctocanumDrive

class RotateAction @JvmOverloads constructor(val heading: Rotation2d, timeout: Double = 5.0) : Action(timeout) {

	val tmpDriveMode = OctocanumDrive.driveMode

	override fun onStart() {
		OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)

	}

	override fun onUpdate() {

	}

	override fun isFinished(): Boolean {
		return Math.abs(heading.rotateBy(OctocanumDrive.getGyroAngle().inverse()).degrees) < 1
	}

	override fun onStop() {
		OctocanumDrive.stop()
		OctocanumDrive.shift(tmpDriveMode)
	}
}