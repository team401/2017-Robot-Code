package org.team401.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.lib.Loop
import org.team401.robot.Constants
import org.team401.robot.ControlBoard
import org.team401.robot.Robot
import org.team401.robot.components.TurretRotator
import org.team401.vision.controller.VisionController

object Turret : Subsystem("turret") {

	enum class TurretState {
		DISABLED, CALIBRATING, MANUAL, SENTRY, AUTO
	}

	private var state = TurretState.DISABLED

	val turretRotator = TurretRotator(CANTalon(Constants.TURRET_ROTATOR))

	private val turretHood = Solenoid(Constants.TURRET_HOOD)
	private val ledRing = Solenoid(Constants.TURRET_LED_RING)

	private var sentryRight = false

	private val minRpm = 2700
	private val maxRpm = 5300
	private val hoodSwitchOn = 150
	private val hoodSwitchOff = 130
	private var rpmOffset = 0
	private var rotateBuffer = 0.0
	private val rotateBufferMax = 0.5

	private val maxRotateSpeed = .14

	private val minRotateSpeed = .06

	private val loop = object : Loop {
		override fun onStart() {
			SmartDashboard.putNumber("flywheel_user_setpoint", 0.0)
		}

		override fun onLoop() {
			if (state == TurretState.DISABLED || state == TurretState.CALIBRATING)
				return
			val vision = Robot.getVisionDataStream()
			var speed = 0

			// rotation code
			if (state >= TurretState.SENTRY) { // auto control
				if (vision.isLatestGoalValid) {
					rotateBuffer = 0.0
					if (!turretHood.get())
						turretHood.set(!(vision.latestGoalDistance > hoodSwitchOff))
					else
						turretHood.set(!(vision.latestGoalDistance > hoodSwitchOn))
					if (track())
						speed = getRpmForDistance()
				} else if (rotateBuffer < rotateBufferMax) {
					rotateBuffer += Constants.LOOP_PERIOD
				} else {
					sentry()
				}

			} else { // manual control
				val turnSpeed = ControlBoard.getTurretYaw()
				// if flywheel isnt spinning, then you can turn
				if (Flywheel.getCurrentState() == Flywheel.FlywheelState.STOPPED) {
					if (Math.abs(turnSpeed) > .25) {
						if (turnSpeed > 0) {
							if (turnSpeed > .9)
								turretRotator.rotate(maxRotateSpeed)
							else
								turretRotator.rotate(minRotateSpeed)
						} else {
							if (turnSpeed < -.9)
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
			}

			// shooting code
			if (state == TurretState.AUTO) {
				if (speed != 0)
					Flywheel.setSpeed(normalizeRPM(speed))
				else
					Flywheel.stop()
			} else if (ControlBoard.getShootFuel().isTriggered) {
				if (speed == 0) {
					// if our speed isnt set, grab it
					val userSetpoint = SmartDashboard.getNumber("flywheel_user_setpoint", 0.0).toInt()
					if (state == TurretState.MANUAL && userSetpoint != 0)
						speed = userSetpoint
					else if (vision.isLatestGoalValid)
						speed = getRpmForDistance()
					else
						speed = (maxRpm - minRpm) / 2
				}
				// fine rpm adjustment from masher
				val delta = -ControlBoard.getTurretThrottle()
				// check that our wanted rpm is smaller than our max
				if (delta > .25 && speed + rpmOffset < maxRpm)
					if (delta > .9)
						rpmOffset += 25
					else
						rpmOffset += 5
				else if (delta < -.25 && speed + rpmOffset > minRpm)
					if (delta < .9)
						rpmOffset -= 25

					else
						rpmOffset -= 5

				Flywheel.setSpeed(normalizeRPM(speed + rpmOffset))
			} else { // dont shoot
				Flywheel.stop()
				rpmOffset = 0
			}
		}

		override fun onStop() {
			setWantedState(TurretState.MANUAL)
			turretRotator.stop()
		}
	}

    init {
        dataLogger.register("turret_position", { turretRotator.getPosition().toInt().toDouble() })
        dataLogger.register("turret_error", { turretRotator.getError().toInt().toDouble() })
        dataLogger.register("vision_distance", { Robot.getVisionDataStream().latestGoalDistance.toInt().toDouble() })
        dataLogger.register("vision_error", { Robot.getVisionDataStream().latestGoalYaw.toInt().toDouble() })
        dataLogger.register("valid_vision_data", { Robot.getVisionDataStream().isLatestGoalValid })
        dataLogger.register("turret_on_target", { turretRotator.onTarget() })
        dataLogger.register("turret_hood_extended", { isHoodExtended()})
        dataLogger.register("limit_switch_triggered", { atZeroPoint() })
        dataLogger.register("sentry_enabled", { state >= TurretState.SENTRY })
        dataLogger.register("auto_shooting_enabled", { state == TurretState.AUTO })
        dataLogger.register("turret_enabled", { state != TurretState.DISABLED })
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
			if (turretRotator.getPosition() > turretRotator.maxAngle - 5)
				sentryRight = true
		}
	}

	private fun track(): Boolean {
		val error = Robot.getVisionDataStream().latestGoalYaw
		if (Math.abs(error) < 3) {
			turretRotator.stop()
			return true
		} else {
			turretRotator.addDegrees(-error)
			return false
		}
	}

	private fun getRpmForDistance(): Int {
		val distance = Robot.getVisionDataStream().latestGoalDistance
		if (!turretHood.get())
			return (9.3446 * distance + 2167.7).toInt()
		else
			return (12.3484 * distance + 1959.1).toInt()
	}

	fun zeroSensors() {
		turretRotator.zero()
	}

	fun extendHood(extended: Boolean) {
		if (state == TurretState.MANUAL || state == TurretState.SENTRY)
			turretHood.set(!extended)
	}

	fun setLedRing(on: Boolean) {
		if (state == TurretState.MANUAL) {
			ledRing.set(on)
			if (on)
				Robot.getVisionController().setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.PROCESSING)
			else
				Robot.getVisionController().setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.STREAMING)
		}
	}

	fun isHoodExtended() = !turretHood.get()

	fun atZeroPoint() = Tower.isTurretLimitSwitchTriggered()

	fun setWantedState(state: TurretState) {
		if (state >= TurretState.SENTRY) {
			ledRing.set(true)
			Robot.getVisionController().setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.PROCESSING)
		} else {
			ledRing.set(false)
			Robot.getVisionController().setCameraMode(VisionController.Camera.GOAL, VisionController.CameraMode.STREAMING)
		}
		this.state = state
	}

	fun getCurrentState() = state

	override fun getSubsystemLoop() = loop
}