package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.hardware.Hardware
import org.team401.robot.Constants
import org.team401.robot.ControlBoard
import org.team401.robot.Robot
import org.team401.robot.loops.Loop

object Hopper : Subsystem() {

    enum class HopperState {
        OFF, ON, INVERTED
    }
    private var state = HopperState.OFF

    private val motor = Hardware.Motors.victorSP(Constants.HOPPER_BOTTOM)
    private var currentVoltage = 0.0
    private var targetVoltage = 0.0
    private val rampRate = Constants.HOPPER_RAMP_RATE * Constants.LOOP_PERIOD

    private val loop = object : Loop {
        override fun onStart() {

        }

        override fun onLoop() {
            if (Intake.getCurrentState() == Intake.IntakeState.ENABLED || Turret.getInstance().isFiring ||
                    ControlBoard.getHopper().isTriggered)
                setWantedState(HopperState.ON)
            else if (state != HopperState.INVERTED)
                setWantedState(HopperState.OFF)

            when (state) {
                HopperState.OFF -> {
                    targetVoltage = 0.0
                    currentVoltage = 0.0
                }
                HopperState.ON ->
                    targetVoltage = 0.8
                HopperState.INVERTED ->
                    targetVoltage = -0.8
                else -> {
                    println("Invalid hopper state $state")
                    state = HopperState.OFF
                }
            }

            updateVoltageRamping()
            motor.speed = currentVoltage
        }

        override fun onStop() {
            motor.speed = 0.0
        }
    }

    fun setWantedState(state: HopperState) {
        this.state = state
    }

    private fun updateVoltageRamping() {
        if (targetVoltage > currentVoltage)
            currentVoltage += rampRate
        else
            currentVoltage -= rampRate
        if (Math.abs(targetVoltage-currentVoltage) < 4*rampRate)
            currentVoltage = targetVoltage
    }

    fun getCurrentState() = state

    override fun getSubsystemLoop(): Loop = loop

    override fun printToSmartDashboard() {
        SmartDashboard.putBoolean("hopper_on", state == HopperState.ON)
        SmartDashboard.putNumber("hopper_current_voltage", motor.speed)
        SmartDashboard.putNumber("hopper_target_voltage", targetVoltage)
    }

}