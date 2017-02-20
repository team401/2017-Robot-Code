package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.DriveStraightAction
import org.team401.robot.auto.actions.RotateAction

class AutoTestMode : AutoMode() {

    override fun routine() {
        runAction(DriveStraightAction(50.0, 500.0))
        runAction(RotateAction(90.0))
        runAction(DriveStraightAction(10.0, 10.0))
    }
}