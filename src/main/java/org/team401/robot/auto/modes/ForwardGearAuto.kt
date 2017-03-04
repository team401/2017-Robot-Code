package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.Turret

class ForwardGearAuto : AutoMode() {

    override fun routine() {
        val forward = arrayOf(CalibrateTurretAction(Turret.TurretState.DISABLED), DriveDistanceAction(-4*12.0)).toList()
        runAction(ParallelAction(forward))
        val reverse = arrayOf(DropGearAction(2.0), DriveDistanceAction(1.5*12)).toList()
        runAction(ParallelAction(reverse))
    }
}