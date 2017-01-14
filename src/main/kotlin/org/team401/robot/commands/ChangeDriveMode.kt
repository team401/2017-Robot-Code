package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive
import org.team401.robot.chassis.OctocanumGearbox

class ChangeDriveMode(val octocanumDrive: OctocanumDrive, val driveMode: OctocanumGearbox.DriveMode, vararg gearboxes: Int) : Command() {

    val toShift = gearboxes

    override fun initialize() = toShift.forEach { octocanumDrive.gearboxes[it].shift(driveMode) }

    override fun execute() = true
}