package org.team401.robot.chassis

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.ADXRS450_Gyro
import edu.wpi.first.wpilibj.PIDController
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.Constants
import org.team401.robot.GyroOutput
import org.team401.robot.math.*
import org.team401.robot.components.OctocanumGearbox
import org.team401.robot.loops.Loop
import org.team401.robot.math.Rotation2d

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
object OctocanumDrive {

    enum class DriveControlState {
        OPEN_LOOP, VELOCITY_SETPOINT, VELOCITY_HEADING_CONTROL, PATH_FOLLOWING_CONTROL
    }

    /**
     * An enum object to represent different drive modes.
     */
    enum class DriveMode {
        TRACTION,
        MECANUM
    }

    var controlState = DriveControlState.OPEN_LOOP


    /**
     * Immutable list of gearboxes, will always have a size of 4
     */
    val gearboxes: Array<OctocanumGearbox> = arrayOf(
            OctocanumGearbox(CANTalon(Constants.FRONT_LEFT_MASTER), CANTalon(Constants.FRONT_LEFT_SLAVE)),
            OctocanumGearbox(CANTalon(Constants.FRONT_RIGHT_MASTER), CANTalon(Constants.FRONT_RIGHT_SLAVE)),
            OctocanumGearbox(CANTalon(Constants.REAR_LEFT_MASTER), CANTalon(Constants.REAR_LEFT_SLAVE)),
            OctocanumGearbox(CANTalon(Constants.REAR_RIGHT_MASTER), CANTalon(Constants.REAR_RIGHT_SLAVE))
    )

    val gyro = ADXRS450_Gyro()
    val gyroError: GyroOutput = GyroOutput()
    val gyroPID: PIDController = PIDController(1.0, 0.0, 0.0, gyro, gyroError)
    val shifter = Solenoid(Constants.GEARBOX_SHIFTER)

    val pidVelocityHeading = SynchronousPID()
    private var velocityHeadingSetpoint: VelocityHeadingSetpoint? = null
    private var lastHeadingErrorDegrees = 0.0

    var brakeModeOn: Boolean = false

    /**
     * The current drive mode of the chassis
     */
    var driveMode = DriveMode.TRACTION

    val driveLoop = object : Loop {
        override fun onStart() {
            setBrakeMode(false)
        }

        override fun onLoop() {
            synchronized(this) {
                when (controlState) {
                    DriveControlState.OPEN_LOOP -> return
                    DriveControlState.VELOCITY_SETPOINT ->
                        // Talons are updating the control loop state
                        return
                    DriveControlState.VELOCITY_HEADING_CONTROL -> {
                        updateVelocityHeadingSetpoint()
                        return
                    }
                    DriveControlState.PATH_FOLLOWING_CONTROL -> {
                        println("we shouldn't be in path following mode!!!")
                        /*updatePathFollower()
                        if (isFinishedPath()) {
                            stop()
                        }*/
                    }
                    else -> System.out.println("Unexpected drive control state: " + controlState)
                }
            }
        }

        override fun onStop() {

        }
    }

    /**
     * Basically the constructor
     */
    fun init() {
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

        gyro.calibrate()
        zeroSensors()
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
        if (controlState != DriveControlState.OPEN_LOOP)
            return
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
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(-wheelSpeeds[Constants.GEARBOX_FRONT_LEFT])
        gearboxes[Constants.GEARBOX_REAR_LEFT].setOutput(-wheelSpeeds[Constants.GEARBOX_REAR_LEFT])
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_FRONT_RIGHT])
        gearboxes[Constants.GEARBOX_REAR_RIGHT].setOutput(wheelSpeeds[Constants.GEARBOX_REAR_RIGHT])

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

    private fun configureTalonsForSpeedControl() {
        if (controlState == DriveControlState.VELOCITY_SETPOINT || controlState == DriveControlState.VELOCITY_HEADING_CONTROL)
            return
        changeControlMode(CANTalon.TalonControlMode.Speed,
                {
                    it.setProfile(Constants.SPEED_CONTROL_PROFILE)
                },
                {
                    it.setProfile(Constants.SPEED_CONTROL_PROFILE)
                },
                {
                    it.changeControlMode(CANTalon.TalonControlMode.Follower)
                    it.set(Constants.FRONT_LEFT_MASTER.toDouble())
                },
                {
                    it.changeControlMode(CANTalon.TalonControlMode.Follower)
                    it.set(Constants.FRONT_RIGHT_MASTER.toDouble())
                })
        setBrakeMode(true)
    }

   private fun configureTalonsForOpenLoopControl() {
        changeControlMode(CANTalon.TalonControlMode.PercentVbus,
                {
                    it.set(0.0)
                    it.setVoltageRampRate(0.0)
                },
                {
                    it.set(0.0)
                    it.setVoltageRampRate(0.0)
                },
                {
                    it.set(0.0)
                },
                {
                    it.set(0.0)
                })
        setBrakeMode(false)
    }

    fun setVelocitySetpoint(leftInchesPerSec: Double, rightInchesPerSec: Double) {
        configureTalonsForSpeedControl()
        controlState = DriveControlState.VELOCITY_SETPOINT
        updateVelocitySetpoint(leftInchesPerSec, rightInchesPerSec)
    }

    @Synchronized fun setVelocityHeadingSetpoint(inchesPerSec: Double, headingSetpoint: Rotation2d) {
        if (controlState != DriveControlState.VELOCITY_HEADING_CONTROL) {
            configureTalonsForSpeedControl()
            controlState = DriveControlState.VELOCITY_HEADING_CONTROL
            pidVelocityHeading.reset()
        }
        velocityHeadingSetpoint = VelocityHeadingSetpoint(inchesPerSec, inchesPerSec,
                headingSetpoint)
        updateVelocityHeadingSetpoint()
    }

    private fun updateVelocitySetpoint(leftInchesPerSec: Double, rightInchesPerSec: Double) {
        gearboxes[Constants.GEARBOX_FRONT_LEFT].setOutput(-inchesPerSecondToRpm(leftInchesPerSec))
        gearboxes[Constants.GEARBOX_FRONT_RIGHT].setOutput(inchesPerSecondToRpm(rightInchesPerSec))
    }

    private fun updateVelocityHeadingSetpoint() {
        val actualGyroAngle = getGyroAngle()

        lastHeadingErrorDegrees = (velocityHeadingSetpoint as VelocityHeadingSetpoint).heading
                .rotateBy(actualGyroAngle.inverse()).getDegrees()

        val deltaSpeed = pidVelocityHeading.calculate(lastHeadingErrorDegrees)
        updateVelocitySetpoint((velocityHeadingSetpoint as VelocityHeadingSetpoint).leftSpeed + deltaSpeed / 2,
                (velocityHeadingSetpoint as VelocityHeadingSetpoint).rightSpeed - deltaSpeed / 2)
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
        return -rotationsToInches(gearboxes[Constants.GEARBOX_FRONT_LEFT].motor.position)
    }

    fun getRightDistanceInches(): Double {
        return rotationsToInches(gearboxes[Constants.GEARBOX_FRONT_RIGHT].motor.position)
    }

    fun getLeftVelocityInchesPerSec(): Double {
        return -rpmToInchesPerSecond(gearboxes[Constants.GEARBOX_FRONT_LEFT].motor.speed)
    }

    fun getRightVelocityInchesPerSec(): Double {
        return rpmToInchesPerSecond(gearboxes[Constants.GEARBOX_FRONT_RIGHT].motor.speed)
    }

    fun setBrakeMode(on: Boolean) {
        if (brakeModeOn != on)
            gearboxes.forEach { it.config { it.enableBrakeMode(on) } }
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
        return Rotation2d.fromDegrees(gyro.getAngle())
    }

    fun printToSmartDashboard() {
        SmartDashboard.putNumber("left_distance", getLeftDistanceInches())
        SmartDashboard.putNumber("right_distance", getRightDistanceInches())
        SmartDashboard.putNumber("left_velocity", getLeftVelocityInchesPerSec())
        SmartDashboard.putNumber("right_velocity", getRightVelocityInchesPerSec())
        SmartDashboard.putNumber("left_error", gearboxes[0].motor.closedLoopError.toDouble());
        SmartDashboard.putNumber("right_error", gearboxes[1].motor.closedLoopError.toDouble());
        SmartDashboard.putNumber("gyro_angle", gyro.angle);
        SmartDashboard.putNumber("heading_error", lastHeadingErrorDegrees);
    }

    /**
     * VelocityHeadingSetpoints are used to calculate the robot's path given the
     * speed of the robot in each wheel and the polar coordinates. Especially
     * useful if the robot is negotiating a turn and to forecast the robot's
     * location.
     */
    data class VelocityHeadingSetpoint(val leftSpeed: Double, val rightSpeed: Double, val heading: Rotation2d)
}
