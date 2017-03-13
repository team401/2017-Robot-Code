package org.team401.robot.auto.actions

import org.team401.robot.Constants
import org.team401.robot.subsystems.GearHolder

class DropGearAction(val duration: Double) : Action() {

	var timer = 0.0

	override fun onStart() {
		GearHolder.setWantedState(GearHolder.GearHolderState.OPEN)
		Thread {
			while (true) {
				onUpdate()
				if (timer > duration) {
					GearHolder.setWantedState(GearHolder.GearHolderState.TOWER_OUT)
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