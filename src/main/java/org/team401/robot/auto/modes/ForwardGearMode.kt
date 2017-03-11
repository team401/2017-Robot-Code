package org.team401.robot.auto.modes

import edu.wpi.first.wpilibj.Timer
import org.team401.robot.auto.AutoMode
import org.team401.robot.auto.actions.*
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.robot.subsystems.Turret

class ForwardGearMode : AutoMode() {

    override fun routine() {
        runAction(CalibrateTurretAction(Turret.TurretState.AUTO))
        OctocanumDrive.shift(OctocanumDrive.DriveMode.MECANUM)
        GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
        // drive up slowly
        runAction(DriveDistanceAction(-4.0*12, 0.6))
        runAction(DriveDistanceAction(-2.0*12, 0.25))
        runAction(DropGearAction(3.0))
        Thread.sleep(1000)
        runAction(DriveDistanceAction(3.0*12, .25))
    }
}