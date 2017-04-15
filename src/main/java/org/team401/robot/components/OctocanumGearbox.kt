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
        motor.setAllowableClosedLoopErr(inchesPerSecondToRpm(4.0).toInt())
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
        motor.set(output)
    }

    fun setBrakeMode(on: Boolean) {
        motor.enableBrakeMode(on)
        slave.enableBrakeMode(on)
    }

    fun getDistanceInches(): Double {
        return rotationsToInches(motor.position)
    }

    fun getVelocityInchesPerSecond(): Double {
        return rpmToInchesPerSecond(motor.speed)
    }

    fun getErrorVelocityInchesPerSecond(): Double {
        return getVelocityInchesPerSecond() - rpmToInchesPerSecond(motor.setpoint)
    }

    fun getDistanceRotations() = motor.position

    fun getVelocityRpm() = motor.speed

    private fun rotationsToInches(rotations: Double): Double {
        return rotations * (Constants.DRIVE_WHEEL_DIAMETER_IN * Math.PI)
    }

    private fun rpmToInchesPerSecond(rpm: Double): Double {
        return rotationsToInches(rpm) / 60
    }

    private fun inchesToRotations(inches: Double): Double {
        return inches / (Constants.DRIVE_WHEEL_DIAMETER_IN * Math.PI)
    }

    private fun inchesPerSecondToRpm(inchesPerSecond: Double): Double {
        return inchesToRotations(inchesPerSecond) * 60
    }
}