package org.team401.robot.loops

import edu.wpi.first.wpilibj.Notifier
import org.team401.robot.Constants
import java.util.ArrayList;

class LoopManager {

	private val period = Constants.LOOP_PERIOD
	private var running = false

	private val lock = Any()
	private val notifier: Notifier
	private val loops: MutableList<Loop> = ArrayList()

	init {
		notifier = Notifier {
			loops.forEach {
				try {
					it.onLoop()
				} catch (e: Exception) {
					println("Error in loop: $it")
				}
			}
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
			synchronized(lock) { loops.forEach { it.onStart()} }
			notifier.startPeriodic(period)
			running = true
		}
	}

	@Synchronized
	fun stop() {
		if (running) {
			println("Stopping periodic loops")
			notifier.stop()
			synchronized(lock) { loops.forEach { it.onStop() } }
			running = false
		}
	}
}