package org.team401.robot.auto.actions

import org.team401.robot.subsystems.Turret

class CalibrateTurretAction(val state: Turret.TurretState) : Action(1.0) {

    override fun onStart() {
        Turret.setWantedState(Turret.TurretState.CALIBRATING)
        Turret.turretRotator.rotate(.14)
    }

    override fun onUpdate() {

    }

    override fun isFinished(): Boolean {
        return Turret.atZeroPoint()
    }

    override fun onInterrupt() {
        Turret.turretRotator.rotate(0.0)
        Turret.setWantedState(Turret.TurretState.DISABLED)
        println("Turret couldn't be calibrated!")
    }

    override fun onStop() {
        Turret.zeroSensors()
        Turret.setWantedState(state)
        println("Turret calibrated!")
    }
}