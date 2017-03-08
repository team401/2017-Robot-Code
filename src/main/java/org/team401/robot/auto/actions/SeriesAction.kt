package org.team401.robot.auto.actions

class SeriesAction(val actions: MutableList<Action>) : Action() {

    var currentAction: Action? = null

    override fun start() {}

    override fun update() {
        if (currentAction == null) {
            currentAction = actions.removeAt(0)
            currentAction?.start()
        }
        currentAction?.update()
        if ((currentAction as Action).isFinished() || (currentAction as Action).isTimedOut()) {
            currentAction?.end()
            currentAction = null
        }
    }

    override fun isFinished(): Boolean {
        return actions.isEmpty() && currentAction == null
    }

    override fun end() {}
}