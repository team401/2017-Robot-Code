package org.team401.robot.components

import com.ctre.CANTalon
import org.strongback.components.Switch
import org.team401.robot.Constants

class TurretRotator(val rotator: CANTalon) {


    val maxAngle = 166.0

    init {
        rotator.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        rotator.set(0.0)
        rotator.reverseOutput(false)
        rotator.reverseSensor(true)
        rotator.configPeakOutputVoltage(2.0, -2.0)
        rotator.enableBrakeMode(true)
        rotator.setForwardSoftLimit(maxAngle*187/5040)
        rotator.setReverseSoftLimit(0.0)
        enableSoftLimits(false)
        rotator.isSafetyEnabled = false
        //TODO: tune pid
        rotator.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
                Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0)
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
        rotator.reset()
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