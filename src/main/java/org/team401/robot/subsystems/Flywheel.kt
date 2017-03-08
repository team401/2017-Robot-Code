package org.team401.robot.subsystems

import com.ctre.CANTalon
import org.team401.robot.Constants
import org.team401.robot.loops.Loop

object Flywheel : Subsystem() {

    enum class FlywheelState {
        OPEN_LOOP, CLOSED_LOOP
    }

    private val master = CANTalon(Constants.TURRET_FLYWHEEL_MASTER)
    private val slave = CANTalon(Constants.TURRET_FLYWHEEL_SLAVE)

    var setpoint = 0.0
    private var state = FlywheelState.OPEN_LOOP

    init {
        master.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
        master.setFeedbackDevice(CANTalon.FeedbackDevice.CtreMagEncoder_Relative)
        master.set(0.0)
        master.reverseOutput(true)
        master.reverseSensor(true)
        master.inverted = true
        master.isSafetyEnabled = false
        master.setPID(Constants.FLYWHEEL_P, Constants.FLYWHEEL_I, Constants.FLYWHEEL_D, Constants.FLYWHEEL_F,
                Constants.FLYWHEEL_IZONE, Constants.FLYWHEEL_RAMP_RATE, 0)
        slave.changeControlMode(CANTalon.TalonControlMode.Follower)
        slave.set(Constants.TURRET_FLYWHEEL_MASTER.toDouble())
    }

    private val loop = object : Loop {
        override fun onStart() {

        }

        override fun onLoop() {
            if (setpoint > 0.0 && state == FlywheelState.OPEN_LOOP) {
                master.changeControlMode(CANTalon.TalonControlMode.Speed)
                master.set(setpoint)
            } else if (setpoint <= 0.0 && state == FlywheelState.CLOSED_LOOP) {
                master.changeControlMode(CANTalon.TalonControlMode.PercentVbus)
            }
        }

        override fun onStop() {

        }
    }

    override fun getSubsystemLoop(): Loop = loop

    override fun printToSmartDashboard() {

    }
}