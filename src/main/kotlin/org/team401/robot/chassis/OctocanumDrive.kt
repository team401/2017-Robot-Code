package org.team401.robot.chassis

import java.util.*

class OctocanumDrive(frontLeftGearbox: OctocanumGearbox, frontRightGearbox: OctocanumGearbox,
                     rearLeftGearbox: OctocanumGearbox, rearRightGearbox: OctocanumGearbox) {

    val gearboxes: MutableList<OctocanumGearbox> = ArrayList()

    init {
        gearboxes.add(frontLeftGearbox)
        gearboxes.add(frontRightGearbox)
        gearboxes.add(rearLeftGearbox)
        gearboxes.add(rearRightGearbox)
    }

    fun shift() = gearboxes.forEach { it.shift() }

    fun shift(driveMode: OctocanumGearbox.DriveMode) = gearboxes.forEach { it.shift(driveMode) }
}
