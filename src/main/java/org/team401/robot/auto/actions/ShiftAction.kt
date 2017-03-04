package org.team401.robot.auto.actions

import org.team401.robot.subsystems.OctocanumDrive

class ShiftAction(val driveMode: OctocanumDrive.DriveMode) : Action {

    override fun start() {
        OctocanumDrive.shift(driveMode)
    }

    override fun update() {

    }

    override fun isFinished(): Boolean {
        return OctocanumDrive.driveMode == driveMode
    }

    override fun end() {

    }
}