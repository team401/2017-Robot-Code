package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.components.Motor
import org.strongback.hardware.Hardware
import org.team401.robot.Constants
import org.team401.robot.loops.Loop

object Intake : Subsystem() {

	enum class IntakeState {
		ARM_UP, ARM_DOWN, ENABLED, CLIMBING
	}

	private var state: IntakeState = IntakeState.ARM_UP
	private val motor = Motor.compose(Hardware.Motors.victorSP(Constants.INTAKE_1),
			Hardware.Motors.victorSP(Constants.INTAKE_2))
	private val solenoid = Solenoid(Constants.ARM_EXTENDER)

	private var currentVoltage = 0.0
	private var targetVoltage = 0.0
	private val rampRate = 4 / 50

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			when (state) {
				IntakeState.ARM_UP -> {
					targetVoltage = 0.0
					solenoid.set(false)
				}
				IntakeState.ARM_DOWN -> {
					targetVoltage = 0.0
					solenoid.set(true)
				}
				IntakeState.ENABLED -> {
					targetVoltage = 0.5
					solenoid.set(true)
				}
				IntakeState.CLIMBING -> {
					targetVoltage = 0.8
					solenoid.set(false)
				}
				else -> {
					println("Invalid intake state $state")
					state = IntakeState.ARM_UP
				}
			}
			updateVoltageRamping()
			motor.speed = currentVoltage
			printToSmartDashboard()
		}

		override fun onStop() {

		}
	}

	fun setWantedState(state: IntakeState) {
		this.state = state
	}

	fun getCurrentState() = state

	fun isArmDown() = state == IntakeState.ENABLED || state == IntakeState.ARM_DOWN

	private fun updateVoltageRamping() {
		if (targetVoltage > currentVoltage)
			currentVoltage += rampRate
		else
			currentVoltage -= rampRate
	}

	override fun getSubsystemLoop(): Loop = loop

	override fun printToSmartDashboard() {
		SmartDashboard.putBoolean("arm_down", state == IntakeState.ENABLED || state == IntakeState.ARM_DOWN)
		SmartDashboard.putNumber("collection_speed", motor.speed)
	}
}