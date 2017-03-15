package org.team401.robot.auto.actions

import java.util.ArrayList

class ParallelAction(val actions: MutableList<Action>) : Action() {

    private val toRemove = ArrayList<Action>()

    override fun onStart() {
        actions.forEach { it.onStart() }
    }

    override fun onUpdate() {
        actions.forEach { it.onUpdate() }
    }

    override fun isFinished(): Boolean {
        actions
                .filter { it.isFinished() || it.isTimedOut() }
                .forEach { toRemove.add(it) }

        toRemove.forEach {
            it.onStop()
            actions.remove(it)
        }
        toRemove.clear()

        return actions.isEmpty()
    }
}