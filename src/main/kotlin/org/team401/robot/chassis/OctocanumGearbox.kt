package org.team401.robot.chassis

import com.ctre.CANTalon

class OctocanumGearbox(val cimMotor: CANTalon, val proMotor: CANTalon) {


    init {
        cimMotor.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)

        proMotor.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)
    }

    fun setSpeed(throttle: Double) {
        cimMotor.setpoint = throttle
        proMotor.setpoint = -throttle
    }
}