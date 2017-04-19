package org.team401.robot.auto.actions

import org.team401.lib.Rotation2d
import org.team401.lib.SynchronousPID
import org.team401.robot.subsystems.OctocanumDrive

class RotateAction @JvmOverloads constructor(val heading: Rotation2d, timeout: Double = 5.0) : Action(timeout) {

	val tmpDriveMode = OctocanumDrive.driveMode
    val headingController = SynchronousPID()

	override fun onStart() {
		OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        headingController.setPID(1.0, 0.0, 0.0)
        headingController.setOutputRange(-.45, .45)

        headingController.setpoint = heading.degrees
	}

	override fun onUpdate() {
        val delta = headingController.calculate(OctocanumDrive.getGyroAngle().degrees)
        OctocanumDrive.setDriveSignal(OctocanumDrive.DriveSignal(delta, -delta))
	}

	override fun isFinished(): Boolean {
		return headingController.onTarget(1.0)
	}

    override fun onInterrupt() {
        onStop()
    }

	override fun onStop() {
		OctocanumDrive.stop()
		OctocanumDrive.shift(tmpDriveMode)
	}
}