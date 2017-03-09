package org.team401.lib

/**
 * An object that can read or output a distance
 */
interface DistanceSensor {
    /**
     * Get the current distance from this object
     */
    fun getDistance(): Double
}