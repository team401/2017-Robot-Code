package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.CalibrateTurretAction
import org.team401.robot.auto.actions.DriveDistanceAction
import org.team401.robot.auto.actions.ParallelAction
import org.team401.robot.subsystems.Turret

class AutoTestMode : AutoMode() {

    override fun routine() {
        val actions = arrayOf(CalibrateTurretAction(Turret.TurretState.DISABLED), DriveDistanceAction(4*12.0)).toList()
        runAction(ParallelAction(actions))
    }

    override fun done() {

    }
}