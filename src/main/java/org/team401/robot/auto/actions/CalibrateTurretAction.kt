package org.team401.robot.auto.actions

import org.team401.robot.subsystems.Turret

class CalibrateTurretAction(val state: Turret.TurretState) : Action(4.0) {

    val turret: Turret = Turret.getInstance()

    override fun start() {
        turret.setWantedState(Turret.TurretState.CALIBRATING)
        turret.turretRotator.rotate(.13)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return turret.atZeroPoint()
    }

    override fun interrupt() {
        turret.turretRotator.rotate(0.0)
        println("Turret couldn't be calibrated!")
    }

    override fun stop() {
        turret.zeroSensors()
        turret.setWantedState(state)
        println("Turret calibrated!")
    }
}