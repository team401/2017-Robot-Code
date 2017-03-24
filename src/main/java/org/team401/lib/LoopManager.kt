package org.team401.lib

import edu.wpi.first.wpilibj.Notifier
import org.team401.lib.CrashTracker
import org.team401.robot.Constants
import java.util.ArrayList

class LoopManager(val period: Double = Constants.LOOP_PERIOD) {

	private var running = false

	private val notifier: Notifier
	private val loops: MutableList<Loop> = ArrayList()

	init {
		notifier = Notifier {
			if (running) {
				loops.forEach {
					try {
						it.onLoop()
					} catch (t: Throwable) {
						CrashTracker.logThrowableCrash(t)
						println("Error in loop: $t")
					}
				}
			}
		}
	}

	@Synchronized
	fun register(loop: Loop) {
		loops.add(loop)
	}

	@Synchronized
	fun start() {
		if (!running) {
			println("Starting periodic loops")
			loops.forEach {
				try {
					it.onStart()
				} catch (t: Throwable) {
					CrashTracker.logThrowableCrash(t)
					println("Error starting loop: $it")
				}
			}
			notifier.startPeriodic(period)
			running = true
		}
	}

	@Synchronized
	fun stop() {
		if (running) {
			println("Stopping periodic loops")
			notifier.stop()
			loops.forEach {
				try {
					it.onStop()
				} catch (t: Throwable) {
					CrashTracker.logThrowableCrash(t)
					println("Error stopping loop: $it")
				}
			}
			running = false
		}
	}
}