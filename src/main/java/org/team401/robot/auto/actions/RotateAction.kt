package org.team401.robot.auto.actions

import org.team401.lib.Rotation2d
import org.team401.robot.subsystems.OctocanumDrive

class RotateAction(val heading: Rotation2d, val power: Double = 0.3, timeout: Double = 5.0) : Action(timeout) {

    val start = OctocanumDrive.getGyroAngle()

    override fun start() {
        if (start.rotateBy(heading.inverse()).degrees < 0)
            OctocanumDrive.drive(0.0, 0.0, power)
        else
            OctocanumDrive.drive(0.0, 0.0, -power)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return Math.abs(heading.inverse().rotateBy(OctocanumDrive.getGyroAngle()).degrees) < 2
    }

    override fun stop() {
        OctocanumDrive.drive(0.0, 0.0)
    }
}