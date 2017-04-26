package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.Servo
import edu.wpi.first.wpilibj.Solenoid
import org.team401.robot.Constants
import org.team401.lib.Loop
import org.team401.lib.VisionBuffer
import org.team401.vision.controller.VisionController

object GearHolder : Subsystem("gear_holder") {

	enum class GearHolderState {
		CLOSED, PUSH_OUT, INTAKE, GEAR_VISION
	}

	private var state = GearHolderState.CLOSED

	private val leftServo = Servo(Constants.SERVO_LEFT)
	private val rightServo = Servo(Constants.SERVO_RIGHT)
	private val solenoid = Solenoid(Constants.GEAR_HOLDER)
    private val ledRing = Solenoid(Constants.GEAR_LED_RING)

	private val gearSensor =  DigitalInput(0)

	private val leftServoHome = 150.0
    private val leftServoProc = 110.0
	private val leftServoOut = 48.0
	private val rightServoHome = 40.0
    private val rightServoProc = 80.0
	private val rightServoOut = 140.0

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			when (state) {
				GearHolderState.CLOSED -> {
					solenoid.set(false)
					leftServo.angle = leftServoHome
					rightServo.angle = rightServoHome
				}
				GearHolderState.PUSH_OUT -> {
					solenoid.set(true)
					leftServo.angle = leftServoHome
					rightServo.angle = rightServoHome
				}
				GearHolderState.INTAKE -> {
					solenoid.set(false)
					leftServo.angle = leftServoOut
					rightServo.angle = rightServoOut
				}
				GearHolderState.GEAR_VISION -> {
                    solenoid.set(false)
                    leftServo.angle = leftServoProc
                    rightServo.angle = rightServoProc
				}
			}
		}

		override fun onStop() {

		}
	}

    init {
        dataLogger.register("has_gear", { hasGear() })
        dataLogger.register("gear_holder_out", { state == GearHolderState.PUSH_OUT })
        dataLogger.register("gear_intake", { state == GearHolderState.INTAKE })
    }

	fun hasGear() = !gearSensor.get()

	fun setWantedState(state: GearHolderState) {
        if (state == GearHolderState.GEAR_VISION)
            VisionBuffer.setGearCameraMode(VisionController.CameraMode.PROCESSING)
        else if (this.state == GearHolderState.GEAR_VISION)
            VisionBuffer.setGearCameraMode(VisionController.CameraMode.STREAMING)
		this.state = state
        ledRing.set(state == GearHolderState.GEAR_VISION)
	}

	fun getCurrentState() = state

	override fun getSubsystemLoop() = loop
}