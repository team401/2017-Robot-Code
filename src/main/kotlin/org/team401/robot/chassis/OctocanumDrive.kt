package org.team401.robot.chassis

import com.analog.adis16448.frc.ADIS16448_IMU
import com.ctre.CANTalon
import edu.wpi.first.wpilibj.ADXRS450_Gyro
import edu.wpi.first.wpilibj.PIDController
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.interfaces.Gyro
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.Constants
import org.team401.robot.GyroOutput
import org.team401.robot.math.MathUtils
import org.team401.robot.components.OctocanumGearbox
import java.util.*

/**
 * Drivetrain wrapper class for the octocanum chassis, supports shifting
 * between drive modes (DriveMode.TRACTION and DriveMode.MECANUM).
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
                     rearLeftGearbox: OctocanumGearbox, rearRightGearbox: OctocanumGearbox,
                     val shifter: Solenoid, val gyro: ADXRS450_Gyro) {
    /**
     * Immutable list of gearboxes, will always have a size of 4
     */
    val gearboxes: List<OctocanumGearbox> = ArrayList()

    val gyroPID: PIDController
    val gyroError: GyroOutput

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

        gyroError = GyroOutput()
        gyroPID = PIDController(1.0, 0.0, 0.0, gyro, gyroError)
        gyroPID.enable()
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
    fun drive(leftYThrottle: Double, leftXThrottle: Double, rightXThrottle: Double) {
        // map the input speeds to match the driver's orientation to the field
        SmartDashboard.putNumber("Gyro Angle", gyro.angle)
        val speed = MathUtils.rotateVector(
                leftXThrottle,
                -leftYThrottle,
                if (driveMode == DriveMode.MECANUM && SmartDashboard.getBoolean("Field-Centric", false))
                    gyro.angle*SmartDashboard.getNumber("Gyro Multiplier", 1.0) else 0.0)

        val x: Double
        if (driveMode == DriveMode.MECANUM)
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
        /*MathUtils.scale(wheelSpeeds, 0.8)
        for (it in wheelSpeeds.indices)
            wheelSpeeds[it] += gyroError.output*/
        SmartDashboard.putNumber("Gyro Error", gyroError.output)

        MathUtils.normalize(wheelSpeeds)
        changeControlMode(CANTalon.TalonControlMode.PercentVbus,
                { it.set(-wheelSpeeds[Constants.GEARBOX_FRONT_LEFT]) },
                { it.set(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT]) },
                { it.set(-wheelSpeeds[Constants.GEARBOX_REAR_LEFT]) },
                { it.set(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT]) })

        SmartDashboard.putData("Gyro Stuff", gyro)
    }

    /**
     * Directly set the output of each gearbox
     */
    fun drive(leftFront: Double, rightFront: Double, leftRear: Double, rightRear: Double) {
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(leftFront)
        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(leftRear)
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(rightFront)
        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(rightRear)
    }

    /**
     * Toggle the drive mode
     */
    fun shift() {
        if (driveMode == DriveMode.TRACTION)
            shift(DriveMode.MECANUM)
        else
            shift(DriveMode.TRACTION)
    }

    /**
     * Set the drive mode to the passed mode. This method does nothing if
     * the passed drive mode is the currently config drive mode.
     *
     * @param driveMode The DriveMode to switch to
     */
    fun shift(driveMode: DriveMode) {
        if (driveMode == DriveMode.TRACTION && this.driveMode == DriveMode.MECANUM) {
            shifter.set(false)
            this.driveMode = DriveMode.TRACTION
        } else if (driveMode == DriveMode.MECANUM && this.driveMode == DriveMode.TRACTION) {
            shifter.set(true)
            this.driveMode = DriveMode.MECANUM
        }
    }

    /**
     * Changes the drive mode of each gearbox, and runs the lambdas on the
     * master CANTalon objects for their respective sides on the robot.
     */
    fun changeControlMode(mode: CANTalon.TalonControlMode, leftFront: (CANTalon) -> Unit, rightFront: (CANTalon) -> Unit,
                          leftRear: (CANTalon) -> Unit, rightRear: (CANTalon) -> Unit) {
        gearboxes.forEach { it.changeControlMode(mode) }
        gearboxes[Constants.GEARBOX_FRONT_LEFT].config(leftFront)
        gearboxes[Constants.GEARBOX_REAR_LEFT].config(leftRear)
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].config(rightFront)
        gearboxes[Constants.GEARBOX_REAR_RIGHT].config(rightRear)
    }

    /**
     * An enum object to represent different drive modes.
     */
    enum class DriveMode {
        TRACTION,
        MECANUM
    }
}
