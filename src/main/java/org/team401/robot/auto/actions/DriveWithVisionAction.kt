package org.team401.robot.auto.actions

import org.team401.lib.Rotation2d
import org.team401.robot.Constants
import org.team401.robot.subsystems.OctocanumDrive

class DriveWithVisionAction @JvmOverloads constructor(val distance: Double, val speed: Double, val heading: () -> Rotation2d, val continuous: Boolean = false, timeout: Double = 5.0) : Action(timeout) {

    val startingPos = DoubleArray(4)
    val gearboxes = OctocanumDrive.gearboxes

    var collision = false

    var x = 0.0
    var y = 0.0

    val accel = OctocanumDrive.accel

    override fun onStart() {
        for (i in 0..3)
            startingPos[i] = OctocanumDrive.gearboxes[i].getDistanceInches()

        OctocanumDrive.setVelocityHeadingSetpoint(speed*12, heading())
    }

    override fun onUpdate() {
        val delta = Constants.ACTION_PERIOD
        collision = Math.abs(accel.x-x)/delta > 150 || Math.abs(accel.y-y)/delta > 150
        x = accel.x
        y = accel.y
        if (collision)
            println("Collision detected!")
    }

    override fun isFinished(): Boolean {
        return startingPos.indices
                .filter { Math.abs(OctocanumDrive.gearboxes[it].getDistanceInches() - startingPos[it]) >= Math.abs(distance*12) }
                .isNotEmpty()
    }

    override fun onInterrupt() {
        onStop()
        println("Couldn't reach distance or encoders are bad!")
    }

    override fun onStop() {
        if (!continuous)
            OctocanumDrive.setVelocityHeadingSetpoint(0.0, heading())
        else
            OctocanumDrive.setControlState(OctocanumDrive.DriveControlState.CLOSED_LOOP)
    }
}