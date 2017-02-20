package org.team401.robot.auto.actions

/**
 * Any kind of one-time action or iterative action that can be preformed
 * during auto. ex. Moving the robot forward, turning the turret...
 */
interface Action {

    /**
     * Called once when the action starts, basically setup for the action
     * or for a single action
     */
    fun start()

    /**
     * Update the action state.
     */
    fun update()

    /**
     * Check if the action is finished.
     */
    fun isFinished(): Boolean

    /**
     * Preform a one-time cleanup
     */
    fun end()
}