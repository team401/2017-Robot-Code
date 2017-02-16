package org.team401.robot.components

import com.ctre.CANTalon
import org.strongback.components.Switch

class TurretRotator(val rotator: CANTalon, val zeroPoint: Switch) {


    val maxAngle = 180.0

    init {
        rotator.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        rotator.set(0.0)
        rotator.reverseOutput(false)
        rotator.reverseSensor(true)
        rotator.configPeakOutputVoltage(2.0, -2.0)
        rotator.enableBrakeMode(true)
        rotator.setForwardSoftLimit(maxAngle*187/5040)
        rotator.enableForwardSoftLimit(true)
        rotator.setReverseSoftLimit(0.0)
        rotator.enableReverseSoftLimit(true)
        rotator.isSafetyEnabled = false
        //TODO: tune pid
        rotator.p = 1.0
        rotator.i = 0.0
        rotator.d = 0.0
        rotator.f = 0.0
    }

    /**
     * Set the position of the turret at a specific angle, must be between 0-360 degrees
     */
    fun setPosition(angle: Double) {
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        rotator.set((angle*187)/(14*360))
    }

    fun getPosition() = getAngle()

    fun addDegrees(angle: Double) {
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        setPosition(getAngle() + angle)
    }

    fun stop() {
        rotator.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        rotator.setpoint = 0.0
    }

    fun zero() {
        rotator.reset()
    }

    fun rotate(throttle: Double) {
        rotator.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        rotator.set(throttle)
    }

    private fun getAngle() = (14*rotator.get())/187 * 360

}