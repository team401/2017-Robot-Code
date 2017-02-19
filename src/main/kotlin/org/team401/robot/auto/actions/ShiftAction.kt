package org.team401.robot.auto.actions

import org.team401.robot.chassis.OctocanumDrive

class ShiftAction(val driveMode: OctocanumDrive.DriveMode) : Action {

    override fun start() {
        OctocanumDrive.shift(driveMode)
        println("Shifted to $driveMode")
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return OctocanumDrive.driveMode == driveMode
    }

    override fun end() {

    }
}