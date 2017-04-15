package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.components.Motor
import org.strongback.hardware.Hardware
import org.team401.robot.Constants
import org.team401.lib.Loop
import org.team401.lib.MathUtils
import org.team401.robot.ControlBoard
import org.team401.robot.Robot

object Intake : Subsystem("intake") {

    enum class IntakeState {
        DISABLED, ARM_DOWN, ENABLED
    }

	private val motor = Motor.compose(Hardware.Motors.victorSP(Constants.INTAKE_1),
			Hardware.Motors.victorSP(Constants.INTAKE_2))
	private val solenoid = Solenoid(Constants.ARM_EXTENDER)

	private var state = IntakeState.DISABLED

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			solenoid.set(state != IntakeState.DISABLED)
            when (state) {
                IntakeState.DISABLED -> {
                    motor.speed = 0.0
                }
                IntakeState.ARM_DOWN -> {
                    motor.speed = 0.0
                }
                IntakeState.ENABLED -> {
                    motor.speed = MathUtils.toRange(ControlBoard.getIntakeThrottle(), 0.25, 1.0, 0.25, 1.0)
                }
            }
            if (state == IntakeState.ENABLED)
                motor.speed = MathUtils.toRange(ControlBoard.getIntakeThrottle(), 0.25, 1.0, 0.25, 1.0)
            else
                motor.speed = 0.0
		}

		override fun onStop() {

		}
	}

    init {
        dataLogger.register("arm_down", { state == IntakeState.ENABLED })
        dataLogger.register("intake_enabled", { state == IntakeState.ENABLED })
    }

    fun setWantedState(state: IntakeState) {
        this.state = state
    }

    fun getCurrentState() = state

	override fun getSubsystemLoop(): Loop = loop
}