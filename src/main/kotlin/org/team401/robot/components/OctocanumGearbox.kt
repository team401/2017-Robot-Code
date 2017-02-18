package org.team401.robot.components

import com.ctre.CANTalon

/**
 * Wrapper class for the octocanum gearbox
 * Motor default mode is PercentVbus
 *
 * @param master CANTalon reference
 * @param slave CANTalon reference
 */
class OctocanumGearbox(val master: CANTalon, val slave: CANTalon) {

    init {
        master.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        master.isSafetyEnabled = false
        slave.changeControlMode(CANTalon.TalonControlMode.Follower)
        slave.isSafetyEnabled = false
        slave.set(master.deviceID.toDouble())
    }

    fun changeControlMode(mode: CANTalon.TalonControlMode) {
        master.changeControlMode(mode)
    }

    /**
     * Takes a lambda that can change settings on the master motor
     */
    fun config(func: (CANTalon) -> Unit) {
        func(master)
    }

    /**
     * Sets the setpoint of the TalonSRX to the specified output
     *
     * @param output for the motor controller
     */
    fun setOutput(output: Double) {
        master.set(output)
    }
}