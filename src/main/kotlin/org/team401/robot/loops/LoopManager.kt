package org.team401.robot.loops

import edu.wpi.first.wpilibj.Notifier
import org.team401.robot.Constants
import java.util.*

class LoopManager {

    private val period = Constants.LOOP_PERIOD
    private var running = false

    private val lock = Any()
    private val notifier: Notifier
    private val loops: MutableList<Loop>

    init {
        loops = ArrayList()
        notifier = Notifier {
            loops.forEach {Loop::onLoop}
        }
    }

    @Synchronized
    fun register(loop: Loop) {
        synchronized(lock) {
            loops.add(loop)
        }
    }

    @Synchronized
    fun start() {
        if (!running) {
            println("Starting periodic loops")
            synchronized(lock) { loops.forEach {Loop::onStart} }
            notifier.startPeriodic(period)
            running = true
        }
    }

    @Synchronized
    fun stop() {
        if (running) {
            println("Stopping periodic loops")
            notifier.stop()
            synchronized(lock) { loops.forEach {Loop::onStop} }
            running = false
        }
    }
}