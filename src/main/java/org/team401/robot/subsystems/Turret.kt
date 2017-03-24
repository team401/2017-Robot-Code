package org.team401.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.lib.Loop
import org.team401.robot.Constants
import org.team401.robot.ControlBoard
import org.team401.robot.Robot
import org.team401.robot.components.TurretRotator

object Turret : Subsystem() {

    enum class TurretState {
        DISABLED, CALIBRATING, MANUAL, SENTRY, AUTO
    }

    var state = TurretState.DISABLED

    val turretRotator = TurretRotator(CANTalon(Constants.TURRET_ROTATOR))

    private val turretHood = Solenoid(Constants.TURRET_HOOD)
    private val ledRing = Solenoid(Constants.TURRET_LED_RING)

    private var sentryRight = false

    private val minRpm = 2500
    private val maxRpm = 4600
    private val distanceHoodSwitch = 0
    private var rpmOffset = 0

    private val maxRotateSpeed = .2
    private val minRotateSpeed = .16

    private val loop = object : Loop {
        override fun onStart() {
            SmartDashboard.putNumber("flywheel_user_setpoint", 0.0)
        }

        override fun onLoop() {
            if (state > TurretState.MANUAL)
                ledRing.set(true)
            else
                ledRing.set(false)

            if (state == TurretState.DISABLED || state == TurretState.CALIBRATING)
                return
            val vision = Robot.getVisionDataStream()
            var speed = 0

            // rotation code
            if (state >= TurretState.SENTRY) { // auto control
                if (vision.isLatestGoalValid && track())
                    speed = getRpmForDistance()
                else
                    sentry()
            } else { // manual control
                val turnSpeed = ControlBoard.getTurretYaw()
                // if flywheel isnt spinning, then you can turn
                if (Flywheel.getCurrentState() == Flywheel.FlywheelState.STOPPED) {
                    if (Math.abs(turnSpeed) > .5) {
                        if (turnSpeed > 0) {
                            if (turnSpeed > .95)
                                turretRotator.rotate(maxRotateSpeed)
                            else
                                turretRotator.rotate(minRotateSpeed)
                        } else {
                            if (turnSpeed < -.95)
                                turretRotator.rotate(-maxRotateSpeed)
                            else
                                turretRotator.rotate(-minRotateSpeed)
                        }
                    } else if (ControlBoard.getTurretSnapLeft().isTriggered) {
                        turretRotator.setPosition(turretRotator.maxAngle)
                    } else if (ControlBoard.getTurretSnapCenter().isTriggered) {
                        turretRotator.setPosition(turretRotator.maxAngle / 2 + 4)
                    } else if (ControlBoard.getTurretSnapRight().isTriggered) {
                        turretRotator.setPosition(0.0)
                    } else {
                        turretRotator.stop()
                    }
                } else {
                    turretRotator.stop()
                }

                // shooting code
                if (state == TurretState.AUTO) {
                    if (speed != 0)
                        Flywheel.setSpeed(speed)
                    else
                        Flywheel.stop()
                } else if (ControlBoard.getShootFuel().isTriggered) {
                    if (speed == 0) {
                        // if our speed isnt set, grab it
                        val userSetpoint = SmartDashboard.getNumber("flywheel_user_setpoint", 0.0).toInt()
                        if (userSetpoint != 0)
                            speed = userSetpoint
                        else if (vision.isLatestGoalValid)
                            speed = getRpmForDistance()
                        else
                            speed = (maxRpm - minRpm) / 2

                        // fine rpm adjustment from masher
                        val delta = -ControlBoard.getTurretThrottle()
                        // check that our wanted rpm is smaller than our max
                        if (delta > .5 && speed + rpmOffset < maxRpm)
                            if (delta > .95)
                                rpmOffset += 100
                            else
                                rpmOffset += 5
                        else if (delta < -.5 && speed + rpmOffset > minRpm)
                            if (delta < .95)
                                rpmOffset -= 100
                            else
                                rpmOffset -= 5

                        Flywheel.setSpeed(normalizeRPM(speed + rpmOffset))
                    } else { // dont shoot
                        Flywheel.stop()
                        rpmOffset = 0
                    }
                }
            }
        }

        override fun onStop() {
            setWantedState(TurretState.MANUAL)
            turretRotator.stop()
        }
    }

    private fun normalizeRPM(speed: Int): Int {
        if (speed > maxRpm)
            return maxRpm
        if (speed < minRpm)
            return minRpm
        return speed
    }

    private fun sentry() {
        if (sentryRight) {
            turretRotator.rotate(maxRotateSpeed)
            if (turretRotator.getPosition() < 5)
                sentryRight = false
        } else {
            turretRotator.rotate(-maxRotateSpeed)
            if (turretRotator.getPosition() > turretRotator.maxAngle-5)
                sentryRight = true
        }
    }

    private fun track(): Boolean {
        val error = Robot.getVisionDataStream().latestGoalYaw
        if (Math.abs(error) < 2) {
            turretRotator.stop()
            return true
        } else {
            turretRotator.addDegrees(-error)
            return false
        }
    }

    private fun getRpmForDistance(): Int {
        val distance = Robot.getVisionDataStream().latestGoalDistance
        val distanceCutoff = distance > distanceHoodSwitch

        turretHood.set(distanceCutoff)
        if (distanceCutoff)
            return (9.3446 * distance + 2167.7).toInt()
        else
            return (12.3484 * distance + 1959.1).toInt()
    }

    fun zeroSensors() {
        turretRotator.zero()
    }

    fun extendHood(extended: Boolean) {
        if (state == TurretState.MANUAL || state == TurretState.SENTRY)
            turretHood.set(extended)
    }

    fun isHoodExtended() = turretHood.get()

    fun atZeroPoint() = Tower.isTurretLimitSwitchTriggered()

    fun setWantedState(state: TurretState) {
        this.state = state
    }

    fun getCurrentState() = state

    override fun getSubsystemLoop() = loop

    override fun printToSmartDashboard() {
        SmartDashboard.putNumber("turret_position", turretRotator.getPosition().toInt().toDouble())
        SmartDashboard.putNumber("turret_error", turretRotator.getError())
        SmartDashboard.putNumber("vision_distance", Robot.getVisionDataStream().latestGoalDistance.toInt().toDouble())
        SmartDashboard.putNumber("vision_error", Robot.getVisionDataStream().latestGoalYaw)
        SmartDashboard.putBoolean("valid_vision_data", Robot.getVisionDataStream().isLatestGoalValid)
        SmartDashboard.putBoolean("turret_on_target", turretRotator.onTarget())
        SmartDashboard.putBoolean("turret_hood_extended", turretHood.get())
        SmartDashboard.putBoolean("limit_switch_triggered", atZeroPoint())
        SmartDashboard.putBoolean("sentry_enabled", state >= TurretState.SENTRY)
        SmartDashboard.putBoolean("auto_shooting_enabled", state == TurretState.AUTO)
        SmartDashboard.putBoolean("turret_enabled", state != TurretState.DISABLED)
    }
}