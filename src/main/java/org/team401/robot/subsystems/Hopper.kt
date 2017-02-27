package org.team401.robot.subsystems

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.hardware.Hardware
import org.team401.robot.Constants
import org.team401.robot.Robot
import org.team401.robot.loops.Loop

object Hopper : Subsystem() {

    enum class HopperState {
        OFF, ON
    }
    private var state = HopperState.OFF

    private val motor = Hardware.Motors.victorSP(Constants.HOPPER_BOTTOM)

    private val loop = object : Loop {
        override fun onStart() {

        }

        override fun onLoop() {
            if (Intake.getCurrentState() == Intake.IntakeState.ENABLED || Robot.getTurret().isFiring)
                setWantedState(HopperState.ON)
            else
                setWantedState(HopperState.OFF)

            when (state) {
                HopperState.OFF ->
                    motor.speed = 0.0
                HopperState.ON ->
                    motor.speed = 0.5
                else ->
                    println("Hopper is in an invalid state!")
            }
        }

        override fun onStop() {
            motor.speed = 0.0
        }
    }

    fun setWantedState(state: HopperState) {
        this.state = state
    }

    override fun getSubsystemLoop(): Loop = loop

    override fun printToSmartDashboard() {
        SmartDashboard.putBoolean("hopper_on", state == HopperState.ON)
    }

}