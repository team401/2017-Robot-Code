package org.team401.robot.commands

import org.strongback.command.Command
import org.strongback.control.SoftwarePIDController
import org.team401.robot.chassis.OctocanumDrive
import org.team401.vision.VisionDataStream.VisionDataStream

class StrafeToGearPeg(octocanumDrive: OctocanumDrive, stream: VisionDataStream) : Command() {

    //val controller: SoftwarePIDController

    init {
        //controller = SoftwarePIDController()
    }

    override fun initialize() {
        super.initialize()
    }

    override fun execute(): Boolean {
        return true
    }

}