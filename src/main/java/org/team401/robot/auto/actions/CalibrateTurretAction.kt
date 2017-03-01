package org.team401.robot.auto.actions

import org.team401.robot.Robot
import org.team401.robot.subsystems.Turret

class CalibrateTurretAction : Action {

    val turret: Turret = Robot.getTurret()

    override fun start() {
        turret.setWantedState(Turret.TurretState.CALIBRATING)
        turret.turretRotator.rotate(.15)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return turret.atZeroPoint()
    }

    override fun end() {
        turret.zeroSensors()
        turret.setWantedState(Turret.TurretState.SENTRY)
    }
}