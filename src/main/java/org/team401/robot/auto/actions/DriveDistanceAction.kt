package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class DriveDistanceAction(val distance: Double) : Action {

    override fun start() {
        OctocanumDrive.resetEncoders()

        if (distance > 0)
            OctocanumDrive.drive(.6, .6)
        else
            OctocanumDrive.drive(-.6, -.7)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        if (distance > 0)
            return OctocanumDrive.getLeftDistanceInches() > distance || OctocanumDrive.getRightDistanceInches() > distance
        else
            return OctocanumDrive.getLeftDistanceInches() < distance || OctocanumDrive.getRightDistanceInches() < distance
    }

    override fun end() {
        OctocanumDrive.drive(0.0, 0.0)
    }
}