package org.team401.robot.chassis

import org.strongback.components.Solenoid
import org.team401.robot.Constants
import org.team401.robot.MathUtils
import java.util.*

class OctocanumDrive(frontLeftGearbox: OctocanumGearbox, frontRightGearbox: OctocanumGearbox,
                     rearLeftGearbox: OctocanumGearbox, rearRightGearbox: OctocanumGearbox, val shifter: Solenoid) {

    val gearboxes: MutableList<OctocanumGearbox> = ArrayList()

    var driveMode = DriveMode.TRACTION

    init {
        gearboxes.add(frontLeftGearbox)
        gearboxes.add(frontRightGearbox)
        gearboxes.add(rearLeftGearbox)
        gearboxes.add(rearRightGearbox)
    }

    fun drive(leftYThrottle: Double, leftXThrottle: Double, rightYThrottle: Double, rightXThrottle: Double) {
        if (driveMode == DriveMode.TRACTION) {
            gearboxes[Constants.GEARBOX_FRONT_LEFT].setSpeed(leftYThrottle)
            gearboxes[Constants.GEARBOX_REAR_LEFT].setSpeed(leftYThrottle)
            gearboxes[Constants.GEARBOX_FRONT_RIGHT].setSpeed(rightYThrottle)
            gearboxes[Constants.GEARBOX_REAR_RIGHT].setSpeed(rightYThrottle)
        } else {
            // drive with orientation to the field
            // TODO add gyro code
            val speed = MathUtils.rotateVector(leftXThrottle, -leftYThrottle, 0.0)

            val x = speed[0]
            val y = speed[1]
            val rot = rightXThrottle

            val wheelSpeeds = DoubleArray(4)
            wheelSpeeds[Constants.GEARBOX_FRONT_LEFT] = x + y + rot
            wheelSpeeds[Constants.GEARBOX_REAR_LEFT] = -x + y + rot
            wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT] = -x + y - rot
            wheelSpeeds[Constants.GEARBOX_REAR_RIGHT] = x + y - rot

            MathUtils.normalize(wheelSpeeds)
            MathUtils.scale(wheelSpeeds, 1.0)
            gearboxes[Constants.GEARBOX_FRONT_LEFT].setSpeed(wheelSpeeds[Constants.GEARBOX_FRONT_LEFT])
            gearboxes[Constants.GEARBOX_REAR_LEFT].setSpeed(wheelSpeeds[Constants.GEARBOX_REAR_LEFT])
            gearboxes[Constants.GEARBOX_FRONT_RIGHT].setSpeed(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT])
            gearboxes[Constants.GEARBOX_REAR_RIGHT].setSpeed(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT])
        }
    }

    fun shift() {
        if (driveMode == DriveMode.TRACTION)
            shift(DriveMode.MECHANUM)
        else
            shift(DriveMode.TRACTION)
    }

    fun shift(driveMode: DriveMode) {
        if (driveMode == DriveMode.TRACTION)
            shifter.retract()
        else
            shifter.extend()
    }

    enum class DriveMode {
        TRACTION,
        MECHANUM
    }
}
