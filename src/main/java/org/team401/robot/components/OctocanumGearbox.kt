package org.team401.robot.components

import com.ctre.CANTalon

/**
 * Wrapper class for the octocanum gearbox
 * Motor default mode is PercentVbus
 *
 * @param motor CANTalon reference
 * @param slave CANTalon reference
 */
class OctocanumGearbox(val motor: CANTalon, private val slave: CANTalon) {

    init {
        motor.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        motor.isSafetyEnabled = false
        motor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        slave.changeControlMode(CANTalon.TalonControlMode.Follower)
        slave.isSafetyEnabled = false
        slave.set(motor.deviceID.toDouble())
    }

    fun changeControlMode(mode: CANTalon.TalonControlMode) {
        motor.changeControlMode(mode)
    }

    /**
     * Takes a lambda that can change settings on the CANTalon
     */
    fun config(func: (CANTalon) -> Unit) {
        func(motor)
    }

    /**
     * Calls set() on the motor CANTalon object
     *
     * @param output for the motor controller
     */
    fun setOutput(output: Double) {
        motor.set(output)
    }

    fun setBrakeMode(on: Boolean) {
        motor.enableBrakeMode(on)
        slave.enableBrakeMode(on)
    }
}