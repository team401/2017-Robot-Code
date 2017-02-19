package org.team401.robot.auto.actions

import com.ctre.CANTalon
import org.team401.robot.Constants
import org.team401.robot.chassis.OctocanumDrive

class DriveStraightAction(val rotations: Double) : Action {

    var startingPositon: Double = 0.0

    override fun start() {
        startingPositon = getCurrentPosition()
        OctocanumDrive.changeControlMode(CANTalon.TalonControlMode.Position,
                {
                    it.p = 1.0
                    it.i = 0.0
                    it.d = 0.0
                    it.f = 0.0
                    it.set(startingPositon + rotations)
                },
                {
                    it.p = 1.0
                    it.i = 0.0
                    it.d = 0.0
                    it.f = 0.0
                    it.set(startingPositon + rotations)
                },
                {
                    it.changeControlMode(CANTalon.TalonControlMode.Follower)
                    it.set(Constants.FRONT_LEFT_MASTER.toDouble())
                },
                {
                    it.changeControlMode(CANTalon.TalonControlMode.Follower)
                    it.set(Constants.FRONT_RIGHT_MASTER.toDouble())
                })
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return Math.abs((startingPositon + rotations) - getCurrentPosition()) < 1
    }

    override fun end() {

    }

    private fun getCurrentPosition(): Double {
        val leftPosition = OctocanumDrive.gearboxes[Constants.GEARBOX_FRONT_LEFT].master.position
        val rightPosition = OctocanumDrive.gearboxes[Constants.GEARBOX_FRONT_RIGHT].master.position
        return (leftPosition + rightPosition) / 2
    }
}