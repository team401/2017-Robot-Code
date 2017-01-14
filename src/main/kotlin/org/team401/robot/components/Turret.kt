package org.team401.robot.components

import com.ctre.CANTalon

class Turret(var enabled: Boolean, val feedController: CANTalon, val shooterController: CANTalon, val hoodController: CANTalon) {

    init {
        feedController.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)

        shooterController.setControlMode(CANTalon.TalonControlMode.Speed.value)

        hoodController.setControlMode(CANTalon.TalonControlMode.Position.value)
    }


}