package org.team401.robot.auto

/**
 * This class selects, runs, and stops (if necessary) a specified autonomous
 * mode.
 */
class AutoModeExecuter(val autoMode: AutoMode) {

	private val thread: Thread

	init {
		thread = Thread {
			autoMode.run()
		}
	}

	fun start() {
		thread.start()
	}

	fun stop() {
		if (thread.isAlive)
			thread.interrupt()
		autoMode.done()
	}
}