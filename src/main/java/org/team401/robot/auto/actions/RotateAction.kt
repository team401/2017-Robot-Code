package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class RotateAction(val heading: Double) : Action {

    override fun start() {
        val currentHeading = OctocanumDrive.gyro?.angle ?: 0.0
        if (currentHeading + heading < currentHeading)
            OctocanumDrive.drive(0.0, 0.0, -.4)
        else
            OctocanumDrive.drive(0.0, 0.0, .4)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return Math.abs(OctocanumDrive.gyro?.angle ?: 0.0 - heading) < 5
    }

    override fun end() {

    }
}