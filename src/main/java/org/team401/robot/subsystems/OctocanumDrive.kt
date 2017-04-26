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
import org.team401.lib.MathUtils.Drive.inchesPerSecondToRpm
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
        OPEN_LOOP, CLOSED_LOOP, VELOCITY_HEADING_CONTROL, PATH_FOLLOWING_CONTROL
    }

    /**
     * An enum object to represent different drive modes.
     */
    enum class DriveMode {
        TRACTION,
        MECANUM
    }

    private var controlState = DriveControlState.CLOSED_LOOP


    /**
     * Immutable list of gearboxes, will always have a size of 4
     */
    val gearboxes: Array<OctocanumGearbox> = arrayOf(
            OctocanumGearbox(CANTalon(Constants.FRONT_LEFT_MASTER), CANTalon(Constants.FRONT_LEFT_SLAVE), true, true),
            OctocanumGearbox(CANTalon(Constants.FRONT_RIGHT_MASTER), CANTalon(Constants.FRONT_RIGHT_SLAVE), false,  true),
            OctocanumGearbox(CANTalon(Constants.REAR_LEFT_MASTER), CANTalon(Constants.REAR_LEFT_SLAVE), true, true),
            OctocanumGearbox(CANTalon(Constants.REAR_RIGHT_MASTER), CANTalon(Constants.REAR_RIGHT_SLAVE), false, true)
    )

    val gyro = ADXRS450_Gyro()
    val accel = BuiltInAccelerometer()
    val shifter = Solenoid(Constants.GEARBOX_SHIFTER)

    private var driveSignal = DriveSignal.NEUTRAL

    val pidVelocityHeading = SynchronousPID()
    private var velocityHeadingSetpoint: VelocityHeadingSetpoint? = null
    private var lastHeadingErrorDegrees = 0.0

    var brakeModeOn: Boolean = false

    /**
     * The current drive mode of the chassis
     */
    var driveMode = DriveMode.TRACTION

    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    private val loop = object : Loop {
        override fun onStart() {

        }

        override fun onLoop() {
            when (controlState) {
                DriveControlState.OPEN_LOOP -> {
                    if (driveSignal != DriveSignal.NEUTRAL) {
                        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(driveSignal.left)
                        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(driveSignal.left)
                        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(driveSignal.right)
                        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(driveSignal.right)
                    } else {
                        val x: Double
                        if (driveMode == DriveMode.MECANUM)
                            x = ControlBoard.getDriveStrafe()
                        else
                            x = 0.0
                        val y = ControlBoard.getDrivePitch()
                        val rot = ControlBoard.getDriveRotate()

                        val wheelSpeeds = DoubleArray(4)
                        wheelSpeeds[Constants.GEARBOX_FRONT_LEFT] = -x + y + rot
                        wheelSpeeds[Constants.GEARBOX_REAR_LEFT] = x + y + rot
                        wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT] = x + y - rot
                        wheelSpeeds[Constants.GEARBOX_REAR_RIGHT] = -x + y - rot
                        MathUtils.scale(wheelSpeeds, 0.9)

                        MathUtils.normalize(wheelSpeeds)

                        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(wheelSpeeds[Constants.GEARBOX_FRONT_LEFT])
                        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(wheelSpeeds[Constants.GEARBOX_REAR_LEFT])
                        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT])
                        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT])
                    }
                }
                DriveControlState.CLOSED_LOOP -> {
                    val x: Double
                    if (driveMode == DriveMode.MECANUM)
                        x = ControlBoard.getDriveStrafe()
                    else
                        x = 0.0
                    val y = ControlBoard.getDrivePitch()
                    val rot = ControlBoard.getDriveRotate()

                    val wheelSpeeds = DoubleArray(4)
                    wheelSpeeds[Constants.GEARBOX_FRONT_LEFT] = -x + y + rot
                    wheelSpeeds[Constants.GEARBOX_REAR_LEFT] = x + y + rot
                    wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT] = x + y - rot
                    wheelSpeeds[Constants.GEARBOX_REAR_RIGHT] = -x + y - rot
                    MathUtils.scale(wheelSpeeds, 1.0)

                    MathUtils.normalize(wheelSpeeds)
                    MathUtils.scale(wheelSpeeds, inchesPerSecondToRpm(Constants.MAX_SPEED*12))

                    gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(wheelSpeeds[Constants.GEARBOX_FRONT_LEFT])
                    gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(wheelSpeeds[Constants.GEARBOX_REAR_LEFT])
                    gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT])
                    gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT])
                }
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
        pidVelocityHeading.setPID(Constants.DRIVE_HEADING_VEL_P, Constants.DRIVE_HEADING_VEL_I,
                Constants.DRIVE_HEADING_VEL_D)

        zeroSensors()

        dataLogger.register("left_front_distance", { gearboxes[Constants.GEARBOX_FRONT_LEFT].getDistanceInches() })
        dataLogger.register("left_rear_distance", { gearboxes[Constants.GEARBOX_REAR_LEFT].getDistanceInches() })
        dataLogger.register("right_front_distance", { gearboxes[Constants.GEARBOX_FRONT_RIGHT].getDistanceInches() })
        dataLogger.register("right_rear_distance", { gearboxes[Constants.GEARBOX_REAR_RIGHT].getDistanceInches() })

        dataLogger.register("left_front_velocity", { gearboxes[Constants.GEARBOX_FRONT_LEFT].getVelocityInchesPerSecond() })
        dataLogger.register("left_rear_velocity", { gearboxes[Constants.GEARBOX_REAR_LEFT].getVelocityInchesPerSecond() })
        dataLogger.register("right_front_velocity", { gearboxes[Constants.GEARBOX_FRONT_RIGHT].getVelocityInchesPerSecond() })
        dataLogger.register("right_rear_velocity", { gearboxes[Constants.GEARBOX_REAR_RIGHT].getVelocityInchesPerSecond() })

        dataLogger.register("left_front_error", { gearboxes[Constants.GEARBOX_FRONT_LEFT].getErrorVelocityInchesPerSecond() })
        dataLogger.register("left_rear_error", { gearboxes[Constants.GEARBOX_REAR_LEFT].getErrorVelocityInchesPerSecond() })
        dataLogger.register("right_front_error", { gearboxes[Constants.GEARBOX_FRONT_RIGHT].getErrorVelocityInchesPerSecond() })
        dataLogger.register("right_rear_error", { gearboxes[Constants.GEARBOX_REAR_RIGHT].getErrorVelocityInchesPerSecond() })

        dataLogger.register("left_front_setpoint", { gearboxes[Constants.GEARBOX_FRONT_LEFT].getSetpoint() })
        dataLogger.register("left_rear_setpoint", { gearboxes[Constants.GEARBOX_REAR_LEFT].getSetpoint() })
        dataLogger.register("right_front_setpoint", { gearboxes[Constants.GEARBOX_FRONT_RIGHT].getSetpoint() })
        dataLogger.register("right_rear_setpoint", { gearboxes[Constants.GEARBOX_REAR_RIGHT].getSetpoint() })

        dataLogger.register("gyro_angle", { getGyroAngle().degrees })
        dataLogger.register("gyro_rate", { gyro.rate })
        dataLogger.register("heading_error", { lastHeadingErrorDegrees })
        dataLogger.register("strafing_enabled", { driveMode == DriveMode.MECANUM })
        dataLogger.register("open_loop_control", { controlState == DriveControlState.OPEN_LOOP })
        dataLogger.register("brake_enabled", { brakeModeOn })
    }

    fun stop() {
        setControlState(DriveControlState.OPEN_LOOP)
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
        if (controlState == DriveControlState.CLOSED_LOOP || controlState == DriveControlState.VELOCITY_HEADING_CONTROL)
            return
        changeControlMode(CANTalon.TalonControlMode.Speed,
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) },
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) },
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) },
                { it.setProfile(Constants.SPEED_CONTROL_PROFILE) })
        setBrakeMode(true)
    }

    fun configureTalonsForOpenLoopControl() {
        if (controlState == DriveControlState.OPEN_LOOP)
            return
        changeControlMode(CANTalon.TalonControlMode.PercentVbus,
                { it.set(0.0) },
                { it.set(0.0) },
                { it.set(0.0) },
                { it.set(0.0) })
        setBrakeMode(true)
        driveSignal = DriveSignal.NEUTRAL
    }

    fun setDriveSignal(driveSignal: DriveSignal) {
        if (controlState != DriveControlState.OPEN_LOOP) {
            configureTalonsForOpenLoopControl()
            controlState = DriveControlState.OPEN_LOOP
        }
        this.driveSignal = driveSignal
    }

    fun setVelocityHeadingSetpoint(inchesPerSec: Double, headingSetpoint: Rotation2d) {
        if (controlState != DriveControlState.VELOCITY_HEADING_CONTROL) {
            configureTalonsForSpeedControl()
            controlState = DriveControlState.VELOCITY_HEADING_CONTROL
            pidVelocityHeading.reset()
            pidVelocityHeading.setOutputRange(-30.0, 30.0)
        }
        velocityHeadingSetpoint = VelocityHeadingSetpoint(-inchesPerSec, -inchesPerSec, headingSetpoint)
        updateVelocityHeadingSetpoint()
    }

    private fun updateVelocitySetpoint(leftInchesPerSec: Double, rightInchesPerSec: Double) {
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(inchesPerSecondToRpm(leftInchesPerSec))
        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(inchesPerSecondToRpm(leftInchesPerSec))
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(inchesPerSecondToRpm(rightInchesPerSec))
        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(inchesPerSecondToRpm(rightInchesPerSec))
    }

    private fun updateVelocityHeadingSetpoint() {
        val actualGyroAngle = getGyroAngle()
        val setpoint = velocityHeadingSetpoint!!

        lastHeadingErrorDegrees = setpoint.heading.rotateBy(actualGyroAngle.inverse()).degrees

        val deltaSpeed = pidVelocityHeading.calculate(lastHeadingErrorDegrees)
        updateVelocitySetpoint(setpoint.leftSpeed - deltaSpeed, setpoint.rightSpeed + deltaSpeed)
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

    fun setControlState(controlState: DriveControlState) {
        if (controlState == DriveControlState.OPEN_LOOP)
            configureTalonsForOpenLoopControl()
        else
            configureTalonsForSpeedControl()
        this.controlState = controlState
    }

    fun getControlState() = controlState

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

    data class DriveSignal(val left: Double, val right: Double) {
        companion object {
            val NEUTRAL = DriveSignal(0.0, 0.0)
        }
    }
}
