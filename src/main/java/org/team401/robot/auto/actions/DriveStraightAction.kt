package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.math.Rotation2d

class DriveStraightAction(val speed: Double, val distance: Double, val heading: Double = 0.0) : Action {

    var startingDistance: Double = 0.0

    override fun start() {
        startingDistance = getCurrentDistance()
        OctocanumDrive.setVelocityHeadingSetpoint(speed, Rotation2d.fromDegrees(heading))
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        var rv: Boolean = false
        if (distance > 0) {
            rv = getCurrentDistance() - startingDistance >= distance
        } else {
            rv = getCurrentDistance() - startingDistance <= distance
        }
        if (rv) {
            OctocanumDrive.setVelocitySetpoint(0.0, 0.0)
        }
        return rv
    }

    override fun end() {
        OctocanumDrive.setVelocitySetpoint(0.0, 0.0)
    }

    private fun getCurrentDistance(): Double {
        return (OctocanumDrive.getLeftDistanceInches() + OctocanumDrive.getRightDistanceInches()) / 2;
    }
}