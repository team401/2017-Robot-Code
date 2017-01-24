package org.team401.robot.components

import com.ctre.CANTalon

class Turret(var enabled: Boolean, val rotationController: CANTalon, val shooterController: CANTalon, val hoodController: CANTalon) {

    init {
        rotationController.setControlMode(CANTalon.TalonControlMode.Position.value)

        shooterController.setControlMode(CANTalon.TalonControlMode.Speed.value)

        hoodController.setControlMode(CANTalon.TalonControlMode.Position.value)
    }


}