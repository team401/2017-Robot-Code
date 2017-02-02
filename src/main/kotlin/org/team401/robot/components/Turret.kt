package org.team401.robot.components

import com.ctre.CANTalon
import org.strongback.components.Motor

class Turret(var enabled: Boolean, val leftSpinner: CANTalon, val rightSpinner: CANTalon, val rotator: CANTalon, val feeder: Motor) {

    init {
        leftSpinner.setControlMode(CANTalon.TalonControlMode.Speed.value)
        rightSpinner.setControlMode(CANTalon.TalonControlMode.Speed.value)
    }
}