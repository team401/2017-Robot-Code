package org.team401.robot.components

import com.ctre.CANTalon
import org.strongback.components.Switch

class TurretRotator(val rotator: CANTalon, val zeroPoint: Switch) {

    val maxAngle = 360.0

    init {
        rotator.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        rotator.setControlMode(CANTalon.TalonControlMode.Position.value)
    }

    /**
     * Set the position of the turret at a specific angle, must be between 0-360 degrees
     */
    fun setPos(angle: Double) = rotator.set((angle*187*4096)/(14*360))

    fun getPos() = getAngle()

    fun addDegrees(angle: Double) {
        if (angle > 0 && getAngle() + angle > maxAngle)
            setPos(maxAngle)
        if (angle < 0 && getAngle() + angle < 0)
            setPos(0.0)
        else
            setPos(getAngle() + angle)
    }

    private fun getAngle() = (14*(rotator.get()/4096))/187 * 360

}