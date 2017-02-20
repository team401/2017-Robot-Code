package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive

/**
 * Command to switch drive modes
 *
 * @param octocanumDrive Reference to the robot's chassis object
 * @param driveMode The drive mode to switch to
 */
class ShiftDriveMode : Command() {

    override fun initialize() = OctocanumDrive.shift()

    override fun execute() = true
}