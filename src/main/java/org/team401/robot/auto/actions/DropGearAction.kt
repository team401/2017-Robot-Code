package org.team401.robot.auto.actions

import org.team401.robot.Constants
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.Tower

class DropGearAction(val duration: Double) : Action() {

	var timer = 0.0

	override fun onStart() {
		GearHolder.setWantedState(GearHolder.GearHolderState.PUSH_OUT)
		Thread {
			while (true) {
				onUpdate()
				if (timer > duration) {
					GearHolder.setWantedState(GearHolder.GearHolderState.CLOSED)
					break
				}
				Thread.sleep((Constants.ACTION_PERIOD*1000).toLong())
			}
		}.start()
	}

	override fun onUpdate() {
		timer += Constants.ACTION_PERIOD
	}

	override fun isFinished(): Boolean {
		return true
	}

	override fun onStop() {

	}
}