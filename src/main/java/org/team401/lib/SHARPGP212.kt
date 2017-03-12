package org.team401.lib

import edu.wpi.first.wpilibj.AnalogInput

/**
 * Wrapper class for the SHARP GP2D12 proximity sensor
 *
 * Reads distance accurately from 10cm - 80cm
 *
 * Default unit is Centimeters
 */
class SHARPGP212(port: Int, val unit: Unit = Unit.CENTIMETERS) : DistanceSensor {

	val input = AnalogInput(port)

	override fun getDistance(): Double {
		return 29.318080199969 * Math.pow(input.averageVoltage, -1.146809657983) * unit.multiplier
	}
}