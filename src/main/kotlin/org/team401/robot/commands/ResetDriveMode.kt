package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive
import org.team401.robot.chassis.OctocanumGearbox

class ResetDriveMode(val octocanumDrive: OctocanumDrive, val driveMode: OctocanumGearbox.DriveMode) : Command() {

    override fun initialize() = octocanumDrive.shift(driveMode)

    override fun execute() = true
}