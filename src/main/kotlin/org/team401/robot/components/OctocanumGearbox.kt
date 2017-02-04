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
        cimMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        proMotor.changeControlMode(CANTalon.TalonControlMode.Follower)
        proMotor.set(cimMotor.deviceID.toDouble())
    }

    fun setControlMode(mode: CANTalon.TalonControlMode) {
        cimMotor.changeControlMode(mode)
    }

    /**
     * Sets the setpoint of the TalonSRX to the specified speed (percent vbus)
     *
     * @param throttle % power for the motors
     */
    fun setSpeed(throttle: Double) {
        cimMotor.setpoint = throttle
    }
}