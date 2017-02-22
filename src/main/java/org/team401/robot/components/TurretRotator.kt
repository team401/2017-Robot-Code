package org.team401.robot.components

import com.ctre.CANTalon
import org.team401.robot.Constants

class TurretRotator(private val rotator: CANTalon) {

    val maxAngle = 166.0

    init {
        rotator.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        rotator.set(0.0)
        rotator.reverseOutput(false)
        rotator.reverseSensor(false)
        rotator.configPeakOutputVoltage(1.0, -1.0)
        rotator.configNominalOutputVoltage(1.0, -1.0)
        rotator.enableBrakeMode(true)
        rotator.setForwardSoftLimit((maxAngle/(Constants.TURRET_GEAR_MULTIPLIER*360)))
        rotator.setReverseSoftLimit(0.0)
        enableSoftLimits(false)
        rotator.isSafetyEnabled = false
        //TODO: tune pid
        rotator.setPID(Constants.ROTATOR_P, Constants.ROTATOR_I, Constants.ROTATOR_D, Constants.ROTATOR_F,
                Constants.ROTATOR_IZONE, Constants.ROTATOR_RAMP_RATE, 0)
    }

    /**
     * Set the position of the turret at a specific angle, in degrees
     */
    fun setPosition(angle: Double) {
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        rotator.set((angle/(Constants.TURRET_GEAR_MULTIPLIER*360)))
    }

    fun getPosition() = rotator.position * Constants.TURRET_GEAR_MULTIPLIER * 360

    fun getSetpoint() = rotator.setpoint * Constants.TURRET_GEAR_MULTIPLIER * 360

    fun getError() = getSetpoint() - getPosition()

    fun onTarget() = getError() < 2

    fun addDegrees(angle: Double) {
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        setPosition(getPosition() + angle)
    }

    fun stop() {
        rotator.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        rotator.setpoint = 0.0
    }

    fun zero() {
        rotator.position = 0.0
        rotator.encPosition = 0
        enableSoftLimits(true)
    }

    fun rotate(throttle: Double) {
        rotator.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        rotator.set(throttle)
    }

    fun enableSoftLimits(enabled: Boolean) {
        rotator.enableForwardSoftLimit(enabled)
        rotator.enableReverseSoftLimit(enabled)
    }
}