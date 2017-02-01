package org.team401.robot.components

import com.ctre.CANTalon

/**
 * Wrapper class for the octocanum gearbox
 *
 * @param cimMotor CANTalon reference
 * @param proMotor CANTalon reference
 */
class OctocanumGearbox(val cimMotor: CANTalon, val proMotor: CANTalon) {

    init {
        cimMotor.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)

        proMotor.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)
    }

    /**
     * Sets the setpoint of the TalonSRX to the specified speed (percent vbus)
     *
     * @param throttle % power for the motors
     */
    fun setSpeed(throttle: Double) {
        cimMotor.setpoint = throttle
        proMotor.setpoint = -throttle
    }
}