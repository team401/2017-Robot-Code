package org.team401.robot.subsystems

import org.team401.lib.Loop

abstract class Subsystem {

	abstract fun getSubsystemLoop(): Loop

	abstract fun printToSmartDashboard()
}