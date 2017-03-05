package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class ForwardGearAuto : AutoMode() {

    override fun routine() {
        //runAction(CalibrateTurretAction(Turret.TurretState.DISABLED))
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        //runAction(DriveDistanceAction(-30.0*12))
    }
}