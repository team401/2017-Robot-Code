package org.team401.robot.auto.actions

import org.team401.robot.subsystems.GearHolder

class DropGearAction(val duration: Double) : Action {

    var timer = 0.0

    override fun start() {
        GearHolder.setWantedState(GearHolder.GearHolderState.OPEN)
    }

    override fun update() {
        timer += 1.0/50.0
    }

    override fun isFinished(): Boolean {
        return timer >= duration
    }

    override fun end() {
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
    }
}