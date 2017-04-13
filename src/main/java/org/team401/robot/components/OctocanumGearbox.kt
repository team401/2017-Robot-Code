package org.team401.robot.components

import com.ctre.CANTalon
import org.team401.robot.Constants

/**
 * Wrapper class for the octocanum gearbox
 * Motor default mode is PercentVbus
 *
 * @param motor CANTalon reference
 * @param slave CANTalon reference
 */
class OctocanumGearbox(val motor: CANTalon, private val slave: CANTalon, inverted: Boolean) {

    init {
        motor.changeControlMode(CANTalon.TalonControlMode.Speed)
        motor.isSafetyEnabled = false
        motor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        motor.setPID(Constants.SPEED_P, Constants.SPEED_I, Constants.SPEED_D, Constants.SPEED_F,
                Constants.SPEED_IZONE, Constants.SPEED_RAMP_RATE, 0)
        motor.setVoltageRampRate(45.0)
        motor.reverseSensor(true)
        slave.changeControlMode(CANTalon.TalonControlMode.Follower)
        slave.isSafetyEnabled = false
        slave.setVoltageRampRate(45.0)
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
        motor.set(output*Constants.MAX_SPEED*12*4*Math.PI)
    }

    fun setBrakeMode(on: Boolean) {
        motor.enableBrakeMode(on)
        slave.enableBrakeMode(on)
    }
}