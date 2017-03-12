package org.team401.robot.auto.actions

class SeriesAction(val actions: MutableList<Action>) : Action() {

    var currentAction: Action? = null

    override fun onStart() {}

    override fun onUpdate() {
        if (currentAction == null) {
            currentAction = actions.removeAt(0)
            currentAction?.onStart()
        }
        currentAction?.onUpdate()
        if ((currentAction as Action).isFinished() || (currentAction as Action).isTimedOut()) {
            currentAction?.onStop()
            currentAction = null
        }
    }

    override fun isFinished(): Boolean {
        return actions.isEmpty() && currentAction == null
    }

    override fun onStop() {}
}