package org.team401.robot.auto.actions

class ParallelAction(val actions: List<Action>) : Action {

    override fun start() {
        actions.forEach(Action::start)
    }

    override fun update() {
        actions.forEach(Action::update)
    }

    override fun isFinished(): Boolean {
        var all_finished = true
        for (action in actions) {
            if (!action.isFinished()) {
                all_finished = false
            }
        }
        return all_finished
    }

    override fun end() {
        actions.forEach(Action::end)
    }
}