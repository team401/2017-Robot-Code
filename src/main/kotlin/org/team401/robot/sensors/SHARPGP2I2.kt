package org.team401.robot.sensors

import edu.wpi.first.wpilibj.AnalogInput

/**
 * Wrapper class for the SHARP GP2DI2 4Y proximity sensor
 *
 * Reads distance accurately from 10cm - 80cm
 */
class SHARPGP2I2(port: Int) : DistanceSensor {

    val input = AnalogInput(port)

    override fun getDistance(): Double = 1 / input.voltage - .42
}