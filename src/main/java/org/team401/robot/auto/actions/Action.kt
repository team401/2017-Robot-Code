package org.team401.robot.auto.actions

import org.strongback.command.Command
import org.team401.robot.Constants

/**
 * Any kind of one-time action or iterative action that can be preformed
 * during auto. ex. Moving the robot forward, turning the turret...
 */
abstract class Action(val timeout: Double = 0.0) {

    var timeoutCounter = 0.0

    /**
     * Called once when the action starts, basically setup for the action
     * or for a single action
     */
    abstract fun onStart()

    /**
     * Update the action state.
     */
    abstract fun onUpdate()

    /**
     * Check if the action is finished.
     */
    abstract fun isFinished(): Boolean

    /**
     * Preform a one-time cleanup
     */
    open fun onStop() {}

    /**
     * Called when the action is interrupted
     */
    open fun onInterrupt() {
        println("Action took too long to finish!")
    }

    fun isTimedOut(): Boolean {
        if (timeout <= 0.0)
            return false
        timeoutCounter += Constants.ACTION_PERIOD
        return timeoutCounter > timeout
    }

    fun asSbCommand(): Command {
        return object : Command() {
            override fun initialize() {
                onStart()
            }
            override fun execute(): Boolean {
                onUpdate()
                return isFinished() || isTimedOut()
            }
            override fun end() {
                onStop()
            }
            override fun interrupted() {
                onInterrupt()
            }
        }
    }
}