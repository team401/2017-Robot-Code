package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.PowerDistributionPanel
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.components.Motor
import org.strongback.hardware.Hardware
import org.team401.robot.Constants
import org.team401.robot.loops.Loop

object Intake : Subsystem() {

    enum class IntakeState {
        DISABLED, ENABLED
    }

	private val motor = Motor.compose(Hardware.Motors.victorSP(Constants.INTAKE_1),
			Hardware.Motors.victorSP(Constants.INTAKE_2))
	private val solenoid = Solenoid(Constants.ARM_EXTENDER)

	private var state = IntakeState.DISABLED

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			solenoid.set(state == IntakeState.ENABLED)
            if (state == IntakeState.ENABLED)
                motor.speed = 1.0
            else
                motor.speed = 0.0

			val pdp = PowerDistributionPanel()
			val avgCurrent = (pdp.getCurrent(0) + pdp.getCurrent(1))
			if (avgCurrent > 40)
				setWantedState(IntakeState.DISABLED)
		}

		override fun onStop() {

		}
	}

    fun setWantedState(state: IntakeState) {
        this.state = state
    }

    fun getCurrentState() = state

	override fun getSubsystemLoop(): Loop = loop

	override fun printToSmartDashboard() {
		SmartDashboard.putBoolean("arm_down", state == IntakeState.ENABLED)
		SmartDashboard.putBoolean("intake_enabled", state == IntakeState.ENABLED)
		SmartDashboard.putNumber("intake_current_voltage", motor.speed)
	}
}