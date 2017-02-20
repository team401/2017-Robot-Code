package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.DriveStraightAction
import org.team401.robot.auto.actions.RotateAction

class AutoTestMode : AutoMode() {

    override fun routine() {
        runAction(DriveStraightAction(100.0, 400.0))
        println("hello")
        runAction(RotateAction(90.0))
        println("hello2")
        //runAction(DriveStraightAction(10.0, 10.0))
    }
}