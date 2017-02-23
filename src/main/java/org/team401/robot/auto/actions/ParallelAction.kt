package org.team401.robot.auto.actions

class ParallelAction(val actions: List<Action>) : Action {

    override fun start() {
        actions.forEach { it.start() }
    }

    override fun update() {
        actions
                .filter { !it.isFinished() }
                .forEach { it.update() }
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
        actions.forEach { it.end() }
    }
}