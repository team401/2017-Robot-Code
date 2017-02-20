package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.CalibrateTurretAction

class CalibrateTurretMode : AutoMode() {

    override fun routine() {
        runAction(CalibrateTurretAction())
    }
}