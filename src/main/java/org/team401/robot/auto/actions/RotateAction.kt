package org.team401.robot.auto.actions

import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.OctocanumDrive

class RotateAction(val heading: Rotation2d, val power: Double = 0.35, timeout: Double = 5.0) : Action(timeout) {

    val start = OctocanumDrive.getGyroAngle()
    val tmpDriveMode = OctocanumDrive.driveMode

    override fun onStart() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        if (start.rotateBy(heading.inverse()).degrees < 0)
            OctocanumDrive.drive(0.0, 0.0, power)
        else
            OctocanumDrive.drive(0.0, 0.0, -power)
    }

    override fun onUpdate() {

    }

    override fun isFinished(): Boolean {
        return Math.abs(heading.inverse().rotateBy(OctocanumDrive.getGyroAngle()).degrees) < 2
    }

    override fun onStop() {
        OctocanumDrive.drive(0.0, 0.0)
        OctocanumDrive.shift(tmpDriveMode)
    }
}