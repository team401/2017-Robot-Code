package org.team401.robot.commands

import org.strongback.command.Command
import org.team401.robot.chassis.ChassisDrive
import org.team401.robot.chassis.ChassisGearbox

class ChangeDriveMode(val chassisDrive: ChassisDrive, val driveMode: ChassisGearbox.DriveMode, vararg gearboxes: Int) : Command() {

    val toShift = gearboxes

    override fun initialize() = toShift.forEach { chassisDrive.gearboxes[it].shift(driveMode) }

    override fun execute() = true
}