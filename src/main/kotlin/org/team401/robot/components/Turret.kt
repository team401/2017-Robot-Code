package org.team401.robot.components

import com.ctre.CANTalon
import org.strongback.components.Motor
import org.strongback.components.Switch

class Turret(var enabled: Boolean, val leftSpinner: CANTalon, val rightSpinner: CANTalon, rotator: CANTalon, rotatorZeroSwitch: Switch, val feeder: Motor) {

    val rotator: TurretRotator

    init {
        leftSpinner.setControlMode(CANTalon.TalonControlMode.Speed.value)
        rightSpinner.setControlMode(CANTalon.TalonControlMode.Speed.value)

        this.rotator = TurretRotator(rotator, rotatorZeroSwitch)
    }
}