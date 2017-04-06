package org.team401.robot.subsystems

import org.team401.lib.DataLogger
import org.team401.lib.FMS
import org.team401.lib.Loop
import org.team401.lib.LoopManager
import org.team401.robot.Robot

abstract class Subsystem(name: String) {

	abstract fun getSubsystemLoop(): Loop

    companion object {
        val dataLoop = LoopManager()
        internal val dataLogger = DataLogger("robot-data", true)

        init {
            dataLogger.register("Alliance", { FMS.getAlliance() })
            dataLogger.register("Alliance Station", { FMS.getAllianceStation() })
            dataLogger.register("Total Voltage", { Robot.getPowerDistributionPanel().voltage })
            dataLogger.register("Total Current", { Robot.getPowerDistributionPanel().totalCurrent })
            for (i in 0..15) {
                val c = i
                dataLogger.register("Current $i", { Robot.getPowerDistributionPanel().getCurrent(c) })
            }
            dataLoop.register(dataLogger)
        }
    }
}