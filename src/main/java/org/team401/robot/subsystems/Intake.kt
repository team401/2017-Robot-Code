package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.components.Motor
import org.strongback.hardware.Hardware
import org.team401.robot.Constants
import org.team401.robot.loops.Loop

object Intake : Subsystem() {

	private val motor = Motor.compose(Hardware.Motors.victorSP(Constants.INTAKE_1),
			Hardware.Motors.victorSP(Constants.INTAKE_2))
	private val solenoid = Solenoid(Constants.ARM_EXTENDER)

	var enabled = false

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			solenoid.set(enabled)
			motor.speed = if (enabled) 1.0 else	0.0
		}

		override fun onStop() {

		}
	}

	override fun getSubsystemLoop(): Loop = loop

	override fun printToSmartDashboard() {
		SmartDashboard.putBoolean("arm_down", enabled)
		SmartDashboard.putBoolean("intake_enabled", enabled)
		SmartDashboard.putNumber("intake_current_voltage", motor.speed)
	}
}