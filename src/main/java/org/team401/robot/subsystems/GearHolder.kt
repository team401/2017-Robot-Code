package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.Constants
import org.team401.robot.loops.Loop

object GearHolder : Subsystem() {

	enum class GearHolderState {
		TOWER_IN, TOWER_OUT, OPEN
	}

	private var state = GearHolderState.TOWER_IN

	private val shift = Solenoid(Constants.TOWER_SHIFTER)
	private val gear = Solenoid(Constants.GEAR_HOLDER)

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			when (state) {
				GearHolderState.TOWER_IN -> {
					shift.set(false)
					gear.set(false)
				}
				GearHolderState.TOWER_OUT -> {
					shift.set(true)
					gear.set(false)
				}
				GearHolderState.OPEN -> {
					shift.set(true)
					gear.set(true)
				}
			}
		}

		override fun onStop() {

		}
	}

	fun setWantedState(state: GearHolderState) {
		this.state = state
	}

	fun getCurrentState() = state

	override fun getSubsystemLoop(): Loop = loop

	override fun printToSmartDashboard() {
		SmartDashboard.putBoolean("tower_extended", state != GearHolderState.TOWER_IN)
		SmartDashboard.putBoolean("gear_holder_open", state == GearHolderState.OPEN)
	}
}