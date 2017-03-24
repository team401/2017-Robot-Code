package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.Tower
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class ForwardGear : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.TRACTION)
        Tower.setWantedState(Tower.TowerState.TOWER_IN)
        // drive up slowly
        runAction(ParallelAction(CalibrateTurretAction(Turret.TurretState.SENTRY), DriveDistanceAction(-3.5*12*2, 0.9)))
        runAction(DriveDistanceAction(-2.5*12*2, 0.4))
        runAction(DropGearAction(3.0))
        Thread.sleep(1000)
        runAction(DriveDistanceAction(2.5*12*2, .4))
        Turret.setWantedState(Turret.TurretState.AUTO)
    }

    override fun done() {
        Tower.setWantedState(Tower.TowerState.TOWER_OUT)
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
    }
}