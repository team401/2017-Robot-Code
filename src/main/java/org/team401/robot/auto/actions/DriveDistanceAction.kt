package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class DriveDistanceAction(val distance: Double, val power: Double = 0.5) : Action {

    var timer = 0.0

    override fun start() {
        OctocanumDrive.resetEncoders()

        if (distance > 0)
            OctocanumDrive.drive(power, power)
        else
            OctocanumDrive.drive(-power, -power)
    }

    override fun update() {
        timer+=1/50.0
        val angle = OctocanumDrive.getGyroAngle().degrees
        if (distance > 0)
            OctocanumDrive.drive(power - angle*.008, power + angle*.008)
        else
            OctocanumDrive.drive(-(power + angle*.008), -(power - angle*.008))
    }

    override fun isFinished(): Boolean {
        if (distance > 0)
            return OctocanumDrive.getLeftDistanceInches() > distance || OctocanumDrive.getRightDistanceInches() > distance || timer > 6.0
        else
            return OctocanumDrive.getLeftDistanceInches() < distance || OctocanumDrive.getRightDistanceInches() < distance || timer > 6.0
    }

    override fun end() {
        OctocanumDrive.drive(0.0, 0.0)
    }
}