package org.team401.robot.auto

import org.team401.lib.CrashTracker
import org.team401.robot.Constants
import org.team401.robot.auto.actions.Action

abstract class AutoMode {

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
        if (!Thread.interrupted())
            action.onStart()
        while (!action.isFinished() && !Thread.interrupted()) {
            action.onUpdate()
            Thread.sleep((Constants.ACTION_PERIOD * 1000.0).toLong())
            if (action.isTimedOut())
                return action.onInterrupt()
        }
    }
}