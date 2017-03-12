package org.team401.robot.auto.actions

import java.util.*

class ParallelAction(val actions: MutableList<Action>) : Action() {

    private val toRemove = ArrayList<Action>()

    override fun start() {
        actions.forEach { it.start() }
    }

    override fun update() {
        actions.forEach { it.update() }
    }

    override fun isFinished(): Boolean {
        actions
                .filter { it.isFinished() || it.isTimedOut() }
                .forEach { toRemove.add(it) }

        toRemove.forEach {
            it.stop()
            actions.remove(it)
        }
        toRemove.clear()

        return actions.isEmpty()
    }
}