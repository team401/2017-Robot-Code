package org.team401.robot.auto.actions

import edu.wpi.first.wpilibj.Timer

class WaitAction(val timeToWait: Double) : Action() {

    var startTime = 0.0

    override fun onStart() {
        startTime = Timer.getFPGATimestamp()
    }

    override fun onUpdate() {

    }

    override fun isFinished(): Boolean {
        return Timer.getFPGATimestamp() - startTime >= timeToWait
    }
}