package org.team401.robot.subsystems

import org.team401.robot.loops.Loop

abstract class Subsystem {

	abstract fun getSubsystemLoop(): Loop

	abstract fun printToSmartDashboard()
}