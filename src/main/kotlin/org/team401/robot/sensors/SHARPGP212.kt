package org.team401.robot.sensors

import edu.wpi.first.wpilibj.AnalogInput

/**
 * Wrapper class for the SHARP GP2D12 proximity sensor
 *
 * Reads distance accurately from 10cm - 80cm
 */
class SHARPGP212(port: Int) : DistanceSensor {

    val input = AnalogInput(port)

    override fun getDistance(): Double {
        return 29.318080199969 * Math.pow(input.voltage, -1.146809657983) / 2.54
    }
}