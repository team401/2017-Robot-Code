package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.Constants
import org.team401.lib.Loop

object Tower : Subsystem() {

	enum class TowerState {
		TOWER_IN, TOWER_OUT
	}

	private var state = TowerState.TOWER_IN

	private val shift = Solenoid(Constants.TOWER_SHIFTER)

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
			when (state) {
				TowerState.TOWER_IN -> {
					shift.set(false)
				}
				TowerState.TOWER_OUT -> {
					shift.set(true)
				}
			}
		}

		override fun onStop() {

		}
	}

	fun setWantedState(state: TowerState) {
		this.state = state
	}

	fun getCurrentState() = state

	override fun getSubsystemLoop(): Loop = loop

	override fun printToSmartDashboard() {
		SmartDashboard.putBoolean("tower_extended", state != TowerState.TOWER_IN)
	}
}