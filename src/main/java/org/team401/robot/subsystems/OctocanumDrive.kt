package org.team401.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.BuiltInAccelerometer
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.lib.ADXRS450_Gyro
import org.team401.lib.MathUtils
import org.team401.lib.SynchronousPID
import org.team401.robot.Constants
import org.team401.robot.components.OctocanumGearbox
import org.team401.lib.Loop
import org.team401.lib.Rotation2d
import org.team401.robot.ControlBoard

/**
 * Drivetrain wrapper class for the octocanum chassis, supports shifting
 * between drive modes (DriveMode.TRACTION and DriveMode.MECANUM).
 *
 * @author Zach Kozar
 * @version 1/15/17
 */
object OctocanumDrive : Subsystem("drive") {

    enum class DriveControlState {
        DRIVER_INPUT, VELOCITY_SETPOINT, VELOCITY_HEADING_CONTROL, PATH_FOLLOWING_CONTROL, IGNORE_INPUT
    }

    /**
     * An enum object to represent different drive modes.
     */
    enum class DriveMode {
        TRACTION,
        MECANUM
    }

    var controlState = DriveControlState.DRIVER_INPUT


    /**
     * Immutable list of gearboxes, will always have a size of 4
     */
    val gearboxes: Array<OctocanumGearbox> = arrayOf(
            OctocanumGearbox(CANTalon(Constants.FRONT_LEFT_MASTER), CANTalon(Constants.FRONT_LEFT_SLAVE), true),
            OctocanumGearbox(CANTalon(Constants.FRONT_RIGHT_MASTER), CANTalon(Constants.FRONT_RIGHT_SLAVE), true),
            OctocanumGearbox(CANTalon(Constants.REAR_LEFT_MASTER), CANTalon(Constants.REAR_LEFT_SLAVE), true),
            OctocanumGearbox(CANTalon(Constants.REAR_RIGHT_MASTER), CANTalon(Constants.REAR_RIGHT_SLAVE), true)
    )

    val gyro = ADXRS450_Gyro()
    val accel = BuiltInAccelerometer()
    val shifter = Solenoid(Constants.GEARBOX_SHIFTER)

    val pidVelocityHeading = SynchronousPID()
    private var velocityHeadingSetpoint: VelocityHeadingSetpoint? = null
    private var lastHeadingErrorDegrees = 0.0

    private var lastSetGyroHeading: Rotation2d? = null

    var brakeModeOn: Boolean = false

    /**
     * The current drive mode of the chassis
     */
    var driveMode = DriveMode.TRACTION

    var x = 0.0
    var y = 0.0
    var z = 0.0

    private val loop = object : Loop {
        override fun onStart() {

        }

        override fun onLoop() {
            when (controlState) {
                DriveControlState.DRIVER_INPUT -> {
                    drive(-ControlBoard.getDrivePitch(), -ControlBoard.getDriveStrafe(), ControlBoard.getDriveRotate())
                }
                DriveControlState.VELOCITY_SETPOINT -> {}
                    // talons are updating the control loop state
                DriveControlState.VELOCITY_HEADING_CONTROL ->
                    updateVelocityHeadingSetpoint()
                DriveControlState.PATH_FOLLOWING_CONTROL -> {
                    println("we shouldn't be in path following mode!!!")
                    /*updatePathFollower()
                    if (isFinishedPath()) {
                        onStop()
                    }*/
                }
                else -> System.out.println("Unexpected drive control state: " + controlState)
            }
            SmartDashboard.putNumber("jerk_x", (accel.x - x) / Constants.LOOP_PERIOD)
            SmartDashboard.putNumber("jerk_y", (accel.y - y) / Constants.LOOP_PERIOD)
            SmartDashboard.putNumber("jerk_z", (accel.z - z) / Constants.LOOP_PERIOD)
            x = accel.x
            y = accel.y
            z = accel.z
        }

        override fun onStop() {

        }
    }

    init {
        gearboxes[Constants.GEARBOX_FRONT_LEFT].config {
            it.reverseSensor(true)
            it.setPID(Constants.SPEED_P, Constants.SPEED_I, Constants.SPEED_D, Constants.SPEED_F,
                    Constants.SPEED_IZONE, Constants.SPEED_RAMP_RATE, Constants.SPEED_CONTROL_PROFILE)
        }
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].config {
            it.reverseSensor(true)
            it.reverseOutput(false)
            it.setPID(Constants.SPEED_P, Constants.SPEED_I, Constants.SPEED_D, Constants.SPEED_F,
                    Constants.SPEED_IZONE, Constants.SPEED_RAMP_RATE, Constants.SPEED_CONTROL_PROFILE)
        }

        pidVelocityHeading.setPID(Constants.DRIVE_HEADING_VEL_P, Constants.DRIVE_HEADING_VEL_I,
                Constants.DRIVE_HEADING_VEL_D)
        pidVelocityHeading.setOutputRange(-30.0, 30.0)

        zeroSensors()

        dataLogger.register("left_distance", { getLeftDistanceInches() })
        dataLogger.register("right_distance", { getRightDistanceInches() })
        dataLogger.register("left_velocity", { getLeftVelocityInchesPerSec() })
        dataLogger.register("right_velocity", { getRightVelocityInchesPerSec() })
        dataLogger.register("left_error", { gearboxes[0].getClosedLoopError() })
        dataLogger.register("right_error", { gearboxes[1].getClosedLoopError() })
        dataLogger.register("gyro_angle", { getGyroAngle().degrees })
        dataLogger.register("heading_error", { lastHeadingErrorDegrees })
        dataLogger.register("strafing_enabled", { driveMode == DriveMode.MECANUM })
        dataLogger.register("open_loop_control", { controlState == DriveControlState.DRIVER_INPUT })
        dataLogger.register("brake_enabled", { brakeModeOn })
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
        if (controlState == DriveControlState.IGNORE_INPUT) {
            return
        } else if (controlState != DriveControlState.DRIVER_INPUT) {
            controlState = DriveControlState.DRIVER_INPUT
            configureTalonsForOpenLoopControl()
        }
        // map the input speeds to match the driver's orientation to the field
        val speed = MathUtils.rotateVector(leftXThrottle, -leftYThrottle, 0.0)

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
        MathUtils.scale(wheelSpeeds, 0.9)

        // try to fix rotation when we dont want it
        if (lastSetGyroHeading != null) {
            lastHeadingErrorDegrees = lastSetGyroHeading!!.rotateBy(getGyroAngle().inverse()).degrees
            if (Math.abs(rot) < .1) {
                val delta = lastHeadingErrorDegrees * 0.01
                wheelSpeeds[Constants.GEARBOX_FRONT_LEFT] -= delta
                wheelSpeeds[Constants.GEARBOX_REAR_LEFT] -= delta
                wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT] += delta
                wheelSpeeds[Constants.GEARBOX_REAR_RIGHT] += delta
            } else
                resetHeadingSetpoint()
        }

        MathUtils.normalize(wheelSpeeds)
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(-wheelSpeeds[Constants.GEARBOX_FRONT_LEFT])
        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(-wheelSpeeds[Constants.GEARBOX_REAR_LEFT])
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT])
        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT])
    }

    fun drive(left: Double, right: Double) {
        if (controlState != DriveControlState.DRIVER_INPUT)
            configureTalonsForOpenLoopControl()
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(-left)
        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(-left)
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(right)
        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(right)
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
     * the passed drive mode is the currently set drive mode.
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
     * motor CANTalon objects for their respective sides on the robot.
     */
    fun changeControlMode(mode: CANTalon.TalonControlMode, leftFront: (CANTalon) -> Unit, rightFront: (CANTalon) -> Unit,
                          leftRear: (CANTalon) -> Unit, rightRear: (CANTalon) -> Unit) {
        gearboxes.forEach { it.changeControlMode(mode) }
        gearboxes[Constants.GEARBOX_FRONT_LEFT].config(leftFront)
        gearboxes[Constants.GEARBOX_REAR_LEFT].config(leftRear)
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].config(rightFront)
        gearboxes[Constants.GEARBOX_REAR_RIGHT].config(rightRear)
    }

    fun zeroSensors() {
        resetEncoders()
        gyro.reset()
    }

    fun configureTalonsForSpeedControl() {
        if (controlState == DriveControlState.VELOCITY_SETPOINT || controlState == DriveControlState.VELOCITY_HEADING_CONTROL)
            return
        changeControlMode(CANTalon.TalonControlMode.Speed,
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) },
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) },
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) },
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) })
        setBrakeMode(true)
    }

    fun configureTalonsForOpenLoopControl() {
        if (controlState == DriveControlState.DRIVER_INPUT)
            return
        changeControlMode(CANTalon.TalonControlMode.PercentVbus,
                { it.set(0.0) },
                { it.set(0.0) },
                { it.set(0.0) },
                { it.set(0.0) })
        setBrakeMode(true)
    }

    fun setVelocitySetpoint(leftInchesPerSec: Double, rightInchesPerSec: Double) {
        configureTalonsForSpeedControl()
        controlState = DriveControlState.VELOCITY_SETPOINT
        updateVelocitySetpoint(leftInchesPerSec, rightInchesPerSec)
    }

    fun setVelocityHeadingSetpoint(inchesPerSec: Double, headingSetpoint: Rotation2d) {
        if (controlState != DriveControlState.VELOCITY_HEADING_CONTROL) {
            configureTalonsForSpeedControl()
            controlState = DriveControlState.VELOCITY_HEADING_CONTROL
            pidVelocityHeading.reset()
        }
        velocityHeadingSetpoint = VelocityHeadingSetpoint(inchesPerSec, inchesPerSec, headingSetpoint)
        updateVelocityHeadingSetpoint()
    }

    private fun updateVelocitySetpoint(leftInchesPerSec: Double, rightInchesPerSec: Double) {
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(-inchesPerSecondToRpm(leftInchesPerSec))
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(inchesPerSecondToRpm(rightInchesPerSec))
    }

    private fun updateVelocityHeadingSetpoint() {
        val actualGyroAngle = getGyroAngle()
        val setpoint = velocityHeadingSetpoint!!

        lastHeadingErrorDegrees = setpoint.heading.rotateBy(actualGyroAngle.inverse()).degrees

        val deltaSpeed = pidVelocityHeading.calculate(lastHeadingErrorDegrees)
        updateVelocitySetpoint((setpoint.leftSpeed + deltaSpeed) / 2, (setpoint.rightSpeed - deltaSpeed) / 2)
    }

    fun setNewHeadingSetpoint() {
        if (controlState != DriveControlState.DRIVER_INPUT) {
            configureTalonsForOpenLoopControl()
            controlState = DriveControlState.DRIVER_INPUT
        }
        lastSetGyroHeading = getGyroAngle()
    }

    fun resetHeadingSetpoint() {
        lastSetGyroHeading = null
    }

    fun setIgnoreInput(on: Boolean) {
        if (on)
            controlState = DriveControlState.IGNORE_INPUT
        else
            controlState = DriveControlState.DRIVER_INPUT
    }

    private fun rotationsToInches(rotations: Double): Double {
        return rotations * (Constants.DRIVE_WHEEL_DIAMETER_IN * Math.PI)
    }

    private fun rpmToInchesPerSecond(rpm: Double): Double {
        return rotationsToInches(rpm) / 60
    }

    private fun inchesToRotations(inches: Double): Double {
        return inches / (Constants.DRIVE_WHEEL_DIAMETER_IN * Math.PI)
    }

    private fun inchesPerSecondToRpm(inchesPerSecond: Double): Double {
        return inchesToRotations(inchesPerSecond) * 60
    }

    fun getLeftDistanceInches(): Double {
        return -rotationsToInches(gearboxes[Constants.GEARBOX_FRONT_LEFT].getPosition())
    }

    fun getRightDistanceInches(): Double {
        return rotationsToInches(gearboxes[Constants.GEARBOX_FRONT_RIGHT].getPosition())
    }

    fun getLeftVelocityInchesPerSec(): Double {
        return -rpmToInchesPerSecond(gearboxes[Constants.GEARBOX_FRONT_LEFT].getSpeed())
    }

    fun getRightVelocityInchesPerSec(): Double {
        return rpmToInchesPerSecond(gearboxes[Constants.GEARBOX_FRONT_RIGHT].getSpeed())
    }

    fun getLeftEncPosition(): Int {
        return gearboxes[Constants.GEARBOX_FRONT_LEFT].getEncPosition()
    }

    fun getRightEncPosition(): Int {
        return gearboxes[Constants.GEARBOX_FRONT_RIGHT].getEncPosition()
    }

    fun setBrakeMode(on: Boolean) {
        if (brakeModeOn != on)
            gearboxes.forEach { it.setBrakeMode(on) }
        brakeModeOn = on
    }

    fun resetEncoders() {
        gearboxes.forEach {
            it.config {
                it.encPosition = 0
                it.position = 0.0
            }
        }
    }

    @Synchronized fun getGyroAngle(): Rotation2d {
        return Rotation2d.fromDegrees(gyro.angle)
    }

    override fun getSubsystemLoop(): Loop = loop

    /**
     * VelocityHeadingSetpoints are used to calculate the robot's path given the
     * speed of the robot in each wheel and the polar coordinates. Especially
     * useful if the robot is negotiating a turn and to forecast the robot's
     * location.
     */
    data class VelocityHeadingSetpoint(val leftSpeed: Double, val rightSpeed: Double, val heading: Rotation2d)
}
