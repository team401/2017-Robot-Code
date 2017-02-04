package org.team401.robot.components

import org.strongback.components.Motor

/**
 * Wrapper class for the octocanum gearbox
 *
 * @param cimMotor CANTalon reference
 * @param proMotor CANTalon reference
 */
class OctocanumGearbox(val cimMotor: Motor, val proMotor: Motor) {

    /**
     * Sets the setpoint of the TalonSRX to the specified speed (percent vbus)
     *
     * @param throttle % power for the motors
     */
    fun setSpeed(throttle: Double) {
        cimMotor.speed = throttle
        proMotor.speed = throttle
    }
}