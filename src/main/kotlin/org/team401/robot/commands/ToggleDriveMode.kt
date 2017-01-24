package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive

/**
 * Command to switch drive modes
 *
 * @param octocanumDrive Reference to the robot's chassis object
 * @param driveMode The drive mode to switch to
 */
class ToggleDriveMode(val octocanumDrive: OctocanumDrive) : Command() {

    override fun initialize() = octocanumDrive.shift()

    override fun execute() = true
}