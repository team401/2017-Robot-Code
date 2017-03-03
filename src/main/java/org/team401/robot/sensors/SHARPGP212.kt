package org.team401.robot.sensors

import edu.wpi.first.wpilibj.AnalogInput

/**
 * Wrapper class for the SHARP GP2D12 proximity sensor
 *
 * Reads distance accurately from 10cm - 80cm
 *
 * Default unit is Centimeters
 */
class SHARPGP212(port: Int, val unit: Unit = SHARPGP212.Unit.CENTIMETERS) : DistanceSensor {

	val input = AnalogInput(port)

	override fun getDistance(): Double {
		return 29.318080199969 * Math.pow(input.averageVoltage, -1.146809657983) * unit.multiplier
	}

	enum class Unit constructor(val multiplier: Double) {
		CENTIMETERS(1.0),
		METERS(0.01),
		INCHES(0.39370),
		FEET(0.032808)
	}
}