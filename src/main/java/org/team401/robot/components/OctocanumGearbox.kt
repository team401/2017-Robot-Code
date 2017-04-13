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
class OctocanumGearbox(private val motor: CANTalon, private val slave: CANTalon, invertedSensor: Boolean) {

    init {
        motor.changeControlMode(CANTalon.TalonControlMode.Speed)
        motor.isSafetyEnabled = false
        motor.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        motor.setPID(Constants.SPEED_P, Constants.SPEED_I, Constants.SPEED_D, Constants.SPEED_F,
                Constants.SPEED_IZONE, Constants.SPEED_RAMP_RATE, Constants.SPEED_CONTROL_PROFILE)
        motor.setVoltageRampRate(45.0)
        motor.reverseSensor(invertedSensor)
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
        val out: Double
        if (motor.controlMode == CANTalon.TalonControlMode.Speed)
            out = output*Constants.MAX_SPEED*12*4*Math.PI
        else
            out = output

        motor.set(out)
    }

    fun setBrakeMode(on: Boolean) {
        motor.enableBrakeMode(on)
        slave.enableBrakeMode(on)
    }

    fun getClosedLoopError() = motor.closedLoopError

    fun getSpeed() = motor.speed

    fun getPosition() = motor.position

    fun getEncPosition() = motor.encPosition
}