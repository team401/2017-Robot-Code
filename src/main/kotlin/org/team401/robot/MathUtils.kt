package org.team401.robot

object MathUtils {

    /**
     * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
     * @param wheelSpeeds the speed of each motor
     */
     fun normalize(wheelSpeeds: DoubleArray) {
        var maxMagnitude = Math.abs(wheelSpeeds[0])
        for (i in wheelSpeeds.indices) {
            val temp = Math.abs(wheelSpeeds[i])
            if (maxMagnitude < temp) maxMagnitude = temp
        }
        if (maxMagnitude > 1.0) {
            for (i in wheelSpeeds.indices) {
                wheelSpeeds[i] = wheelSpeeds[i] / maxMagnitude
            }
        }
    }

    /**
     * Scale all speeds.
     * @param wheelSpeeds the speed of each motor
     * *
     * @param scaleFactor the scale factor to apply to the motor speeds
     */
     fun scale(wheelSpeeds: DoubleArray, scaleFactor: Double) {
        for (i in wheelSpeeds.indices) {
            wheelSpeeds[i] = wheelSpeeds[i] * scaleFactor
        }
    }

    /**
     * Rotate a vector in Cartesian space.
     * @param x the x value of the vector
     * *
     * @param y the y value of the vector
     * *
     * @param angle the angle to rotate
     * *
     * @return the vector of x and y values
     */
    fun rotateVector(x: Double, y: Double, angle: Double): DoubleArray {
        val angleInRadians = Math.toRadians(angle)
        val cosA = Math.cos(angleInRadians)
        val sinA = Math.sin(angleInRadians)
        val out = DoubleArray(2)
        out[0] = x * cosA - y * sinA
        out[1] = x * sinA + y * cosA
        return out
    }
}