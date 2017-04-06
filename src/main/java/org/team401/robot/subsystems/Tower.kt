package org.team401.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.Solenoid
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.Constants
import org.team401.lib.Loop

object Tower : Subsystem("tower") {

	enum class TowerState {
		TOWER_IN, TOWER_OUT, KICKER_ON, KICKER_INVERTED
	}

	private var state = TowerState.TOWER_IN

	private val shift = Solenoid(Constants.TOWER_SHIFTER)
	private val motor = CANTalon(Constants.TURRET_FEEDER)

	init {
		motor.enableLimitSwitch(true, false)
		motor.set(0.0)
	}

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {
            if (Flywheel.isWithinTolerance())
                setWantedState(TowerState.KICKER_ON)
            if (Flywheel.getCurrentState() == Flywheel.FlywheelState.STOPPED && state == TowerState.KICKER_ON)
                setWantedState(TowerState.TOWER_OUT)

			when (state) {
				TowerState.TOWER_IN -> {
					shift.set(false)
					motor.set(0.0)
				}
				TowerState.TOWER_OUT -> {
					shift.set(true)
					motor.set(0.0)
				}
				TowerState.KICKER_ON -> {
					shift.set(true)
					motor.set(1.0)
				}
				TowerState.KICKER_INVERTED -> {
					shift.set(true)
					motor.set(-1.0)
				}
			}
		}

		override fun onStop() {

		}
	}

    init {
        dataLogger.register("tower_extended", { state != TowerState.TOWER_IN })
        dataLogger.register("kicker_throttle", { motor.get() })
    }

	fun isTurretLimitSwitchTriggered() = motor.isRevLimitSwitchClosed

	fun setWantedState(state: TowerState) {
		this.state = state
	}

	fun getCurrentState() = state

	override fun getSubsystemLoop(): Loop = loop
}