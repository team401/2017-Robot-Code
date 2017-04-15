package org.team401.lib

import org.team401.robot.Constants

object MathUtils {

    /**
     * Normalize all wheel speeds if the magnitude of any wheel is greater than 1.0.
     * @param array the speed of each motor
     */
    fun normalize(array: DoubleArray) {
        var maxMagnitude = Math.abs(array[0])
        for (i in array.indices) {
            val temp = Math.abs(array[i])
            if (maxMagnitude < temp)
                maxMagnitude = temp
            if (temp < .05)
                array[i] = 0.0
        }
        if (maxMagnitude > 1.0) {
            for (i in array.indices) {
                array[i] = array[i] / maxMagnitude
            }
        }
    }

    /**
     * Scale all speeds.
     * @param array the speed of each motor
     * *
     * @param scaleFactor the scale factor to apply to the motor speeds
     */
    fun scale(array: DoubleArray, scaleFactor: Double) {
        for (i in array.indices) {
            array[i] = array[i] * scaleFactor
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

    /**
     * Map a value from one range to another.
     *
     * @param x value to be mapped
     * @param min minimum value of x
     * @param max maximum value of x
     * @param newMin new minimum value of x
     * @param newMax new maximum of x
     */
    fun toRange(x: Double, min: Double, max: Double, newMin: Double, newMax: Double): Double {
        return (newMax - newMin) * (x - min) / (max - min) + newMin
    }
    
    object Drive {
        fun rotationsToInches(rotations: Double): Double {
            return rotations * (Constants.DRIVE_WHEEL_DIAMETER_IN * Math.PI)
        }

        fun rpmToInchesPerSecond(rpm: Double): Double {
            return rotationsToInches(rpm) / 60
        }

        fun inchesToRotations(inches: Double): Double {
            return inches / (Constants.DRIVE_WHEEL_DIAMETER_IN * Math.PI)
        }

        fun inchesPerSecondToRpm(inchesPerSecond: Double): Double {
            return inchesToRotations(inchesPerSecond) * 60
        }
    }
}