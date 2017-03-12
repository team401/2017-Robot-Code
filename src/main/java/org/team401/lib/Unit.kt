package org.team401.lib

/**
 * Default unit (multiplier of 1.0) is centimeters
 */
enum class Unit(val multiplier: Double) {
    CENTIMETERS(1.0),
    METERS(1.0 / 100.0),
    INCHES(1.0 / 2.54),
    FEET(1.0 / 2.54 / 12.0)
}