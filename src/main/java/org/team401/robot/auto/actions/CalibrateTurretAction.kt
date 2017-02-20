package org.team401.robot.auto.actions

import org.team401.robot.Robot
import org.team401.robot.components.Turret

class CalibrateTurretAction : Action {

    val turret: Turret = Robot.getTurret()

    override fun start() {
        turret.turretRotator.rotate(0.8)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return turret.atZeroPoint().isTriggered
    }

    override fun end() {
        turret.turretRotator.stop()
        turret.turretRotator.zero()
    }
}