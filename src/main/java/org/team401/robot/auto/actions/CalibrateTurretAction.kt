package org.team401.robot.auto.actions

import org.team401.robot.subsystems.Turret

class CalibrateTurretAction(val state: Turret.TurretState) : Action(1.0) {

    val turret: Turret = Turret.getInstance()

    override fun onStart() {
        turret.setWantedState(Turret.TurretState.CALIBRATING)
        turret.turretRotator.rotate(.25)
    }

    override fun onUpdate() {

    }

    override fun isFinished(): Boolean {
        return turret.atZeroPoint()
    }

    override fun onInterrupt() {
        turret.turretRotator.rotate(0.0)
        turret.setWantedState(Turret.TurretState.DISABLED)
        println("Turret couldn't be calibrated!")
    }

    override fun onStop() {
        turret.zeroSensors()
        turret.setWantedState(Turret.TurretState.MANUAL)//state)
        println("Turret calibrated!")
    }
}