package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.ChassisDrive
import org.team401.robot.chassis.ChassisGearbox

class ResetDriveMode(val chassisDrive: ChassisDrive, val driveMode: ChassisGearbox.DriveMode) : Command() {

    override fun initialize() = chassisDrive.shift(driveMode)

    override fun execute() = true
}