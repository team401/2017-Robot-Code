package org.team401.robot.commands

import edu.wpi.first.wpilibj.Solenoid
import org.strongback.command.Command

class SetGearHolderPosition(val solenoid: Solenoid, val open: Boolean) : Command() {

    override fun initialize() {
        solenoid.set(open)
    }

    override fun execute(): Boolean {
        return true
    }
}