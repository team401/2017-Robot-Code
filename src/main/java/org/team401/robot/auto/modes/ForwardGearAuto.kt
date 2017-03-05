package org.team401.robot.auto.modes

import edu.wpi.first.wpilibj.Timer
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive

class ForwardGearAuto : AutoMode() {

    override fun routine() {
        //runAction(CalibrateTurretAction(Turret.TurretState.DISABLED))
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        runAction(DriveDistanceAction(-7.0*12, .4))
        runAction(DropGearAction(2.0))
        Timer.delay(1.0)
        runAction(DriveDistanceAction(3.0*12))
    }
}