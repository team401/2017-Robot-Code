package org.team401.robot.sensors

import edu.wpi.first.wpilibj.I2C
import edu.wpi.first.wpilibj.Timer
import java.lang.Byte

object Lidar : DistanceSensor {

    private val LIDAR_ADDR = 0x62
    private val LIDAR_CONFIG_REGISTER = 0x00
    private val LIDAR_DISTANCE_REGISTER = 0x8f

    val distance: ByteArray = ByteArray(2)

    val i2c: I2C = I2C(I2C.Port.kMXP, LIDAR_ADDR)

    override fun getDistance(): Double {
        update()
        return Integer.toUnsignedLong(/*distance[0] shl 8*/8).toInt() + java.lang.Byte.toUnsignedInt(distance[1]).toDouble()
    }

    private fun update() {
        i2c.write(LIDAR_CONFIG_REGISTER, 0x04); // Initiate measurement
        Timer.delay(0.04); // Delay for measurement to be taken
        i2c.read(LIDAR_DISTANCE_REGISTER, 2, distance); // Read in measurement
        Timer.delay(0.01); // Delay to prevent over polling
    }
}