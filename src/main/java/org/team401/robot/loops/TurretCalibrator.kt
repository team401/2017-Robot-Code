package org.team401.robot.loops

import org.team401.lib.Loop
import org.team401.robot.subsystems.Turret

class TurretCalibrator : Loop {

    override fun onStart() {

    }

    override fun onLoop() {
        if (Turret.atZeroPoint())
            Turret.zeroSensors()
    }

    override fun onStop() {

    }
}