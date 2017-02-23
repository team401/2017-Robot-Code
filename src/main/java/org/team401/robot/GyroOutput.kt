package org.team401.robot

import edu.wpi.first.wpilibj.PIDOutput

class GyroOutput : PIDOutput {

	var output: Double = 0.0

	override fun pidWrite(output: Double) {
		this.output = output
	}
}