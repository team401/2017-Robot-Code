package org.team401.robot.auto

import org.strongback.Strongback.stop


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
}