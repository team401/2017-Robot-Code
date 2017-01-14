package org.team401.robot.chassis

import java.util.*

class ChassisDrive(frontLeftGearbox: ChassisGearbox, frontRightGearbox: ChassisGearbox,
                   rearLeftGearbox: ChassisGearbox, rearRightGearbox: ChassisGearbox) {

    val gearboxes: MutableList<ChassisGearbox> = ArrayList()

    init {
        gearboxes.add(frontLeftGearbox)
        gearboxes.add(frontRightGearbox)
        gearboxes.add(rearLeftGearbox)
        gearboxes.add(rearRightGearbox)
    }

    fun shift() = gearboxes.forEach { it.shift() }

    fun shift(driveMode: ChassisGearbox.DriveMode) = gearboxes.forEach { it.shift(driveMode) }
}
