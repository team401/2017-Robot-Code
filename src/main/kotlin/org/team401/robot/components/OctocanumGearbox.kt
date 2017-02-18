package org.team401.robot.components

import com.ctre.CANTalon

/**
 * Wrapper class for the octocanum gearbox
 * Motor default mode is PercentVbus
 *
 * @param cimMotor CANTalon reference
 * @param proMotor CANTalon reference
 */
class OctocanumGearbox(val cimMotor: CANTalon, val proMotor: CANTalon) {

    init {
        cimMotor.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        cimMotor.isSafetyEnabled = false
        proMotor.changeControlMode(CANTalon.TalonControlMode.Follower)
        proMotor.isSafetyEnabled = false
        proMotor.set(cimMotor.deviceID.toDouble())
    }

    fun changeControlMode(mode: CANTalon.TalonControlMode) {
        cimMotor.changeControlMode(mode)
    }

    /**
     * Takes a lambda that can change settings on the master motor
     */
    fun config(func: (CANTalon) -> Unit) {
        func(cimMotor)
    }

    /**
     * Sets the setpoint of the TalonSRX to the specified output
     *
     * @param output for the motor controller
     */
    fun setOutput(output: Double) {
        cimMotor.set(output)
    }
}