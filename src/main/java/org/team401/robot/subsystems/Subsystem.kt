package org.team401.robot.subsystems

import org.team401.lib.DataLogger
import org.team401.lib.FMS
import org.team401.lib.Loop
import org.team401.lib.LoopManager
import org.team401.robot.Robot

abstract class Subsystem(name: String) {

    internal val dataLogger = DataLogger(name, true)

	abstract fun getSubsystemLoop(): Loop

    init {
        dataLoop.register(dataLogger)
    }

    companion object {
        val dataLoop = LoopManager()
        private val dl = DataLogger("robot", false)

        init {
            dl.register("Alliance", { FMS.getAlliance() })
            dl.register("Alliance Station", { FMS.getAllianceStation() })
            dl.register("Total Voltage", { Robot.getPowerDistributionPanel().voltage })
            dl.register("Total Current", { Robot.getPowerDistributionPanel().totalCurrent })
            for (i in 0..15) {
                val c = i
                dl.register("Current $i", { Robot.getPowerDistributionPanel().getCurrent(c) })
            }
            dataLoop.register(dl)
        }
    }
}