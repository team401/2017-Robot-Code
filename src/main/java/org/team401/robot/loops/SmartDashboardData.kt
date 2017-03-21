package org.team401.robot.loops

import org.team401.lib.CrashTracker
import org.team401.lib.Loop
import org.team401.robot.subsystems.Subsystem
import java.util.*

class SmartDashboardData(private val data: MutableList<Subsystem> = ArrayList()) : Loop {

    override fun onLoop() = data.forEach {
        try {
            it.printToSmartDashboard()
        } catch (t: Throwable) {
            CrashTracker.logThrowableCrash(t)
        }
    }

    fun register(subsystem: Subsystem) = data.add(subsystem)

    override fun onStart() {}

    override fun onStop() {}
}