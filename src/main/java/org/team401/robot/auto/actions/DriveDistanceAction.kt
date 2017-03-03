package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class DriveDistanceAction(val distance: Double) : Action {

    var leftDistance = 0.0
    var rightDistance = 0.0

    override fun start() {
        leftDistance = OctocanumDrive.getLeftDistanceInches()
        rightDistance = OctocanumDrive.getRightDistanceInches()

        OctocanumDrive.drive(.5, .5)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        if (distance > 0)
            return OctocanumDrive.getLeftDistanceInches() - leftDistance > distance || OctocanumDrive.getRightDistanceInches() - rightDistance > distance
        else
            return OctocanumDrive.getLeftDistanceInches() - leftDistance < distance || OctocanumDrive.getRightDistanceInches() - rightDistance < distance
    }

    override fun end() {
        OctocanumDrive.drive(0.0, 0.0)
    }
}