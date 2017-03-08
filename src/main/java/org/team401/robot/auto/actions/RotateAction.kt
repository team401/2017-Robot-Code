package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class RotateAction(val heading: Double, timeout: Double = 5.0) : Action(timeout) {

    override fun start() {
        OctocanumDrive.gyro?.reset()
        if (heading > 0)
            OctocanumDrive.drive(0.0, 0.0, .2)
        else
            OctocanumDrive.drive(0.0, 0.0, -.2)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return Math.abs(OctocanumDrive.getGyroAngle().degrees - heading) < 1
    }

    override fun end() {

    }
}