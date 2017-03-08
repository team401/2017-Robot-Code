package org.team401.robot.auto.actions

import edu.wpi.first.wpilibj.Timer
import org.team401.robot.subsystems.GearHolder

class DropGearAction(val duration: Double) : Action() {

    var timer = 0.0

    override fun start() {
        GearHolder.setWantedState(GearHolder.GearHolderState.OPEN)
        Thread {
            while (true) {
                update()
                if (timer > duration) {
                    GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
                    break
                }
                Timer.delay(1.0/50)
            }
        }.start()
    }

    override fun update() {
        timer += 1.0/50.0
    }

    override fun isFinished(): Boolean {
        return true
    }

    override fun end() {

    }
}