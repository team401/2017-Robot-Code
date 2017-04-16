package org.team401.robot.components

import com.ctre.CANTalon
import org.team401.lib.MathUtils.Drive.inchesPerSecondToRpm
import org.team401.lib.MathUtils.Drive.rotationsToInches
import org.team401.lib.MathUtils.Drive.rpmToInchesPerSecond
import org.team401.robot.Constants

/**
 * Wrapper class for the octocanum gearbox
 * Motor default mode is PercentVbus
 *
 * @param master CANTalon reference
 * @param slave CANTalon reference
 */
class OctocanumGearbox(private val master: CANTalon, private val slave: CANTalon, invertedOutput: Boolean, invertedSensor: Boolean) {

    init {
        master.changeControlMode(CANTalon.TalonControlMode.Speed)
        master.isSafetyEnabled = false
        master.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        master.setPID(Constants.SPEED_P, Constants.SPEED_I, Constants.SPEED_D, Constants.SPEED_F,
                Constants.SPEED_IZONE, Constants.SPEED_RAMP_RATE, Constants.SPEED_CONTROL_PROFILE)
        master.setAllowableClosedLoopErr(inchesPerSecondToRpm(4.0).toInt())
        master.setVoltageRampRate(45.0)
        master.inverted = invertedOutput
        master.reverseSensor(invertedSensor)
        slave.changeControlMode(CANTalon.TalonControlMode.Follower)
        slave.inverted = invertedOutput
        slave.isSafetyEnabled = false
        slave.setVoltageRampRate(45.0)
        slave.set(master.deviceID.toDouble())
    }

    fun changeControlMode(mode: CANTalon.TalonControlMode) {
        master.changeControlMode(mode)
    }

    /**
     * Takes a lambda that can change settings on the CANTalon
     */
    fun config(func: (CANTalon) -> Unit) {
        func(master)
    }

    /**
     * Calls set() on the master CANTalon object
     *
     * @param output for the master controller
     */
    fun setOutput(output: Double) {
        master.set(output)
    }

    fun setBrakeMode(on: Boolean) {
        master.enableBrakeMode(on)
        slave.enableBrakeMode(on)
    }

    fun getDistanceInches(): Double {
        return rotationsToInches(getDistanceRotations())
    }

    fun getVelocityInchesPerSecond(): Double {
        return rpmToInchesPerSecond(getVelocityRpm())
    }

    fun getErrorVelocityInchesPerSecond(): Double {
        return getVelocityInchesPerSecond() - rpmToInchesPerSecond(master.setpoint)
    }

    fun getDistanceRotations() = master.position

    fun getVelocityRpm() = master.speed
}