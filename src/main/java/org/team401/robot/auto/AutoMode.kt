package org.team401.robot.auto

import org.team401.lib.CrashTracker
import org.team401.robot.auto.actions.Action

abstract class AutoMode {

    val updateRate = 1.0 / 50

    abstract fun routine()

    fun run() {
        try {
            routine()
            done()
            println("Auto Finished!")
        } catch (e: Throwable) {
            CrashTracker.logThrowableCrash(e)
            println("Auto ended early due to crash!")
        }
    }

    open fun done() {}

    /**
     * Run an action, blocks the thread until the action is completed.
     */
    fun runAction(action: Action) {
        action.start()
        while (!action.isFinished()) {
            try {
                action.update()
                Thread.sleep((updateRate * 1000.0).toLong())
            } catch (e: Throwable) {
                CrashTracker.logThrowableCrash(e)
            }
        }
        action.end()
    }
}