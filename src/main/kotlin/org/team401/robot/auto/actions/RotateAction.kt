package org.team401.robot.auto.actions

import org.team401.robot.chassis.OctocanumDrive

class RotateAction(val heading: Double) : Action {

    override fun start() {
        val currentHeading = OctocanumDrive.gyro.angle
        if (currentHeading + heading < currentHeading)
            OctocanumDrive.drive(0.0, 0.0, -.4)
        else
            OctocanumDrive.drive(0.0, 0.0, .4)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return Math.abs(OctocanumDrive.gyro.angle - heading) < 5
    }

    override fun end() {

    }
}