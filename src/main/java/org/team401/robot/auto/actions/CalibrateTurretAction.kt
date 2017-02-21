package org.team401.robot.auto.actions

import org.team401.robot.Robot
import org.team401.robot.subsystems.Turret

class CalibrateTurretAction : Action {

    val turret: Turret = Robot.getTurret()

    override fun start() {
        turret.enableSentry(false)
        turret.turretRotator.setPosition(-turret.turretRotator.maxAngle)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return turret.atZeroPoint()
    }

    override fun end() {
        turret.turretRotator.stop()
        turret.turretRotator.zero()
        turret.enableSentry(true)
    }
}