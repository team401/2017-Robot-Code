package org.team401.robot.auto

import org.team401.robot.subsystems.OctocanumDrive

/**
 * This class selects, runs, and stops (if necessary) a specified autonomous
 * mode.
 */
class AutoModeExecutor(val autoMode: AutoMode) {

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
        OctocanumDrive.controlState = OctocanumDrive.DriveControlState.DRIVER_INPUT
    }
}