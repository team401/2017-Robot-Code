package org.team401.robot.auto.modes

import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.DriveDistanceAction
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive

class DriveForwardMode : AutoMode() {

    override fun routine() {
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        runAction(DriveDistanceAction(-15.0*12))
    }
}