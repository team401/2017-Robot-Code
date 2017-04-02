package org.team401.robot.loops

import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.lib.FMS
import org.team401.lib.Loop
import org.team401.robot.subsystems.GearHolder
import org.team401.robot.subsystems.OctocanumDrive

class LedManager : Loop {

	val controller = Joystick(2)

	override fun onStart() {

	}

	override fun onLoop() {
		controller.setOutput(0, FMS.getMatchTime() in 0..29)
		controller.setOutput(1, GearHolder.hasGear())
		controller.setOutput(2, OctocanumDrive.driveMode == OctocanumDrive.DriveMode.TRACTION)
	}

	override fun onStop() {

	}
}