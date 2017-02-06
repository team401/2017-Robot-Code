package org.team401.robot.chassis

import edu.wpi.first.wpilibj.Solenoid
import org.team401.robot.Constants
import org.team401.robot.MathUtils
import org.team401.robot.components.OctocanumGearbox
import java.util.*

/**
 * Drivetrain wrapper class for the octocanum chassis, supports shifting
 * between drive modes (DriveMode.TRACTION and DriveMode.MECHANUM).
 *
 * @param frontLeftGearbox Reference to the gearbox with talons 2 and 3
 * @param frontRightGearbox Reference to the gearbox with talons 4 and 5
 * @param rearLeftGearbox Reference to the gearbox with talons 6 and 7
 * @param rearRightGearbox Reference to the gearbox with talons 8 and 9
 *
 * @author Zach Kozar
 * @version 1/15/17
 */
class OctocanumDrive(frontLeftGearbox: OctocanumGearbox, frontRightGearbox: OctocanumGearbox,
                     rearLeftGearbox: OctocanumGearbox, rearRightGearbox: OctocanumGearbox, val shifter: Solenoid) {
    /**
     * Immutable list of gearboxes, will always have a size of 4
     */
    val gearboxes: List<OctocanumGearbox> = ArrayList()

    /**
     * The current drive mode of the chassis
     */
    var driveMode = DriveMode.TRACTION

    init {
        // add gearbox references to an array to make it easier to iterate through them
        // cast to a mutable array so we can actually add objects
        gearboxes as MutableList<OctocanumGearbox>
        gearboxes.add(frontLeftGearbox)
        gearboxes.add(frontRightGearbox)
        gearboxes.add(rearLeftGearbox)
        gearboxes.add(rearRightGearbox)
    }

    /**
     * Takes in joystick inputs from two joysticks and sets the speed of the talon controllers
     *
     * This method automatically switches it's driving logic based on the current drive mode.
     *
     * @param leftYThrottle Left joystick's getPitch() value
     * @param leftXThrottle Left joystick's getRoll() value
     * @param rightXThrottle Right joystick's getPitch() value
     * @param rightYThrottle Right joysticks getRoll() value
     */
    fun drive(leftYThrottle: Double, leftXThrottle: Double, rightYThrottle: Double, rightXThrottle: Double) {
        // drive with orientation to the field
        // TODO add gyro code

        // map the input speeds to match the driver's orientation to the field
        val speed = MathUtils.rotateVector(leftXThrottle, -leftYThrottle, 0.0)

        val x: Double
        if (driveMode == DriveMode.MECHANUM)
            x = speed[0]
        else
            x = 0.0
        val y = speed[1]
        val rot = rightXThrottle

        val wheelSpeeds = DoubleArray(4)
        wheelSpeeds[Constants.GEARBOX_FRONT_LEFT] = x + y + rot
        wheelSpeeds[Constants.GEARBOX_REAR_LEFT] = -x + y + rot
        wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT] = -x + y - rot
        wheelSpeeds[Constants.GEARBOX_REAR_RIGHT] = x + y - rot

        MathUtils.normalize(wheelSpeeds)
        // MathUtils.scale(wheelSpeeds, 1.0) scaling to 1 does nothing!
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setSpeed(-wheelSpeeds[Constants.GEARBOX_FRONT_LEFT])
        gearboxes[Constants.GEARBOX_REAR_LEFT].setSpeed(-wheelSpeeds[Constants.GEARBOX_REAR_LEFT])
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setSpeed(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT])
        gearboxes[Constants.GEARBOX_REAR_RIGHT].setSpeed(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT])
    }

    /**
     * Toggle the drive mode
     */
    fun shift() {
        if (driveMode == DriveMode.TRACTION)
            shift(DriveMode.MECHANUM)
        else
            shift(DriveMode.TRACTION)
    }

    /**
     * Set the drive mode to the passed mode. This method does nothing if
     * the passed drive mode is the currently set drive mode.
     *
     * @param driveMode The DriveMode to switch to
     */
    fun shift(driveMode: DriveMode) {
        if (driveMode == DriveMode.TRACTION && this.driveMode == DriveMode.MECHANUM) {
            shifter.set(false)
            this.driveMode = DriveMode.TRACTION
        } else if (driveMode == DriveMode.MECHANUM && this.driveMode == DriveMode.TRACTION) {
            shifter.set(true)
            this.driveMode = DriveMode.MECHANUM
        }
    }

    /**
     * An enum object to represent different drive modes
     */
    enum class DriveMode {
        TRACTION,
        MECHANUM
    }
}
