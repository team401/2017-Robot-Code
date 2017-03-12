package org.team401.robot.auto.actions

import com.ctre.CANTalon
import jaci.pathfinder.Waypoint
import jaci.pathfinder.Pathfinder
import jaci.pathfinder.Trajectory
import jaci.pathfinder.followers.EncoderFollower
import jaci.pathfinder.modifiers.TankModifier
import org.team401.robot.Constants
import org.team401.robot.subsystems.OctocanumDrive


class FollowWaypointAction(waypoints: Array<Waypoint>) : Action() {

    val left: EncoderFollower
    val right: EncoderFollower

    init {
        val config = Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0)
        val trajectory = Pathfinder.generate(waypoints, config)
        val modifier = TankModifier(trajectory).modify(0.5842)

        left = EncoderFollower(modifier.leftTrajectory)
        right = EncoderFollower(modifier.rightTrajectory)
    }

    override fun start() {
        left.configureEncoder(OctocanumDrive.getLeftEncPosition(), 4096, 4.0)
        right.configureEncoder(OctocanumDrive.getRightEncPosition(), 4096, 4.0)
        left.configurePIDVA(1.0, 0.0, 0.0, 1 / 1.7, 0.0)
        right.configurePIDVA(1.0, 0.0, 0.0, 1 / 1.7, 0.0)
        OctocanumDrive.changeControlMode(CANTalon.TalonControlMode.PercentVbus,
                { it.set(0.0) },
                { it.set(0.0) },
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
        val l = left.calculate(OctocanumDrive.getLeftEncPosition())
        val r = right.calculate(OctocanumDrive.getRightEncPosition())

        val currentHeading = OctocanumDrive.getGyroAngle().degrees
        val desiredHeading = left.heading

        val error = Pathfinder.boundHalfDegrees(desiredHeading - currentHeading)
        val turn = 0.8 * (-1.0 / 80.0) * error

        OctocanumDrive.drive(l + turn, r - turn)
    }

    override fun isFinished(): Boolean {
        return left.isFinished && right.isFinished
    }

    override fun stop() {

    }
}