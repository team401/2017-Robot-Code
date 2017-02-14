package org.team401.robot.components

import com.ctre.CANTalon
import org.strongback.components.Switch

class TurretRotator(val rotator: CANTalon, val zeroPoint: Switch) {


    val maxAngle = 180.0

    init {
        rotator.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        rotator.changeControlMode(CANTalon.TalonControlMode.Position)
        rotator.set(0.0)
        rotator.enableBrakeMode(true)
        rotator.setForwardSoftLimit(maxAngle*187/5040)
        rotator.enableForwardSoftLimit(true)
        rotator.setReverseSoftLimit(0.0)
        rotator.enableReverseSoftLimit(true)
        //TODO: tune pid
        rotator.p = 0.001
        rotator.i = 0.0
        rotator.d = 0.0
        rotator.f = 0.1
    }

    /**
     * Set the position of the turret at a specific angle, must be between 0-360 degrees
     */
    fun setPosition(angle: Double) = rotator.set((angle*187)/(14*360))

    fun getPosition() = getAngle() * 1

    fun addDegrees(angle: Double) {
        if (angle > 0 && getAngle() + angle > maxAngle)
            setPosition(maxAngle)
        else if (angle < 0 && getAngle() + angle < 0)
            setPosition(0.0)
        else
            setPosition(getAngle() + angle)
    }

    fun stop() {
        rotator.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        rotator.setpoint = 0.0
    }

    fun zero() {
        rotator.reset()
    }

    fun rotate(speed: Double) {
        rotator.setpoint = speed
    }

    private fun getAngle() = (14*(rotator.get()))/187 * 360

}