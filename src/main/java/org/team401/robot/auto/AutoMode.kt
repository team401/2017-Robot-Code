package org.team401.robot.auto

import org.team401.robot.auto.actions.Action

abstract class AutoMode {

    val updateRate = 1.0 / 50

    abstract fun routine()

    fun run() {
        routine()
        done()
        println("Auto Finished!")
    }

    open fun done() {}

    /**
     * Run an action, blocks the thread until the action is completed.
     */
    fun runAction(action: Action) {
        action.start()
        while (!action.isFinished()) {
            action.update()
            try {
                Thread.sleep((updateRate * 1000.0).toLong())
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        action.end()
    }
}