package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive

class ChangeDriveMode(val octocanumDrive: OctocanumDrive, val driveMode: OctocanumDrive.DriveMode, vararg gearboxes: Int) : Command() {

    val toShift = gearboxes

    override fun initialize() = octocanumDrive.shift(driveMode)

    override fun execute() = true
}