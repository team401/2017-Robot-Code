package org.team401.robot.subsystems

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.Constants
import org.team401.lib.Loop

object Flywheel : Subsystem() {

	enum class FlywheelState {
		STOPPED, RUNNING
	}

	private var state = FlywheelState.STOPPED

	private val master = CANTalon(Constants.TURRET_FLYWHEEL_MASTER)
	private val slave = CANTalon(Constants.TURRET_FLYWHEEL_SLAVE)

	private val loop = object : Loop {
		override fun onStart() {

		}

		override fun onLoop() {

		}

		override fun onStop() {

		}

	}

	init {
		master.isSafetyEnabled = false
		master.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
		master.configPeakOutputVoltage(12.0, 0.0)
		master.reverseSensor(true)
		master.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
				Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0)

		/*slave.isSafetyEnabled = false
		slave.inverted = true
		slave.changeControlMode(CANTalon.TalonControlMode.Follower)
		slave.set(Constants.TURRET_FLYWHEEL_MASTER.toDouble())*/
	}

	fun setSpeed(speed: Double) {
		if (state == FlywheelState.STOPPED) {
			master.changeControlMode(CANTalon.TalonControlMode.Speed)
			state = FlywheelState.RUNNING
		}
		master.set(speed)
	}

	fun getSpeed() = master.speed

	fun stop() {
		if (state == FlywheelState.RUNNING) {
			master.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
			state = FlywheelState.STOPPED
		}
		master.set(0.0)
	}

	fun getCurrentState() = state

	override fun getSubsystemLoop() = loop

	override fun printToSmartDashboard() {
		SmartDashboard.putNumber("flywheel_rpm", Math.round(master.speed).toDouble())
		SmartDashboard.putNumber("flywheel_talon_setpoint", Math.round(master.setpoint).toDouble())
		SmartDashboard.putNumber("flywheel_error", master.closedLoopError.toDouble())
	}
}