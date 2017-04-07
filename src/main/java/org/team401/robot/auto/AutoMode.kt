package org.team401.robot.auto

import org.team401.lib.CrashTracker
import org.team401.lib.FMS
import org.team401.robot.Constants
import org.team401.robot.auto.actions.Action

abstract class AutoMode {

    companion object {
        const val dStatToAir = 5.3*12 // driver station to turning angle to airship
        const val dAirToGear = 3.5 *12 // ^^ to gear peg
        const val dGearToBaseL = 3.5*12 // ^^ back to base line
        const val dBaseLToHop = 5.0*12 // ^^ to hopper
        const val dBaseToReload = 8.0*12 // baseline off to reloading station
    }

    abstract fun routine()

    fun run() {
        try {
            routine()
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
        action.onStop()
    }
}