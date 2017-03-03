package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.subsystems.Turret

class CalibrateTurretMode : AutoMode() {

    override fun routine() {
        runAction(CalibrateTurretAction(Turret.TurretState.SENTRY))
    }
}