package org.team401.robot.commands

import edu.wpi.first.wpilibj.PIDController
import edu.wpi.first.wpilibj.PIDSource
import edu.wpi.first.wpilibj.PIDSourceType
import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive
import org.team401.vision.VisionDataStream.VisionDataStream

class StrafeToGearPeg(octocanumDrive: OctocanumDrive, stream: VisionDataStream) : Command() {

    val controller: PIDController

    init {
        controller = PIDController(1, 0, 0, StrafeError(stream))
        stream.latestGearData.strafe.
    }

    override fun initialize() {
        super.initialize()
    }

    override fun execute(): Boolean {
        return true
    }

    class StrafeError(val stream: VisionDataStream) : PIDSource {

        override fun getPIDSourceType(): PIDSourceType {
            return PIDSourceType.kDisplacement
        }

        override fun setPIDSourceType(pidSource: PIDSourceType) {}

        override fun pidGet(): Double {
            return stream.latestGearData.yaw
        }

    }

}