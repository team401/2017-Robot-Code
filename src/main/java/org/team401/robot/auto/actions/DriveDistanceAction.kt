package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class DriveDistanceAction(val distance: Double, val power: Double = 0.5, timeout: Double = 5.0) : Action(timeout) {

    val heading = OctocanumDrive.getGyroAngle()

    override fun start() {
        OctocanumDrive.resetEncoders()

        if (distance > 0)
            OctocanumDrive.drive(power, power)
        else
            OctocanumDrive.drive(-power, -power)
    }

    override fun update() {
        val angle = heading.inverse().rotateBy(OctocanumDrive.getGyroAngle()).degrees
        if (distance > 0)
            OctocanumDrive.drive(power - angle*.01, power + angle*.01)
        else
            OctocanumDrive.drive(-(power + angle*.01), -(power - angle*.01))
    }

    override fun isFinished(): Boolean {
        if (distance > 0)
            return OctocanumDrive.getLeftDistanceInches() > distance && OctocanumDrive.getRightDistanceInches() > distance
        else
            return OctocanumDrive.getLeftDistanceInches() < distance && OctocanumDrive.getRightDistanceInches() < distance
    }

    override fun interrupt() {
        stop()
        println("Couldn't reach distance or encoders are bad!")
    }

    override fun stop() {
        OctocanumDrive.drive(0.0, 0.0)
    }
}