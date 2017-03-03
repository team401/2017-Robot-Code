package org.team401.lib

import com.ctre.CANTalon

class MotionProfile(val name: String, private val positions: DoubleArray,
                             private val speeds: DoubleArray, private val durations: IntArray) {

    var currentIndex = 0
    var totalPoints = positions.size

    fun getNextTrajectoryPoint(): CANTalon.TrajectoryPoint {
        val point = CANTalon.TrajectoryPoint()
        if (currentIndex >= totalPoints) {
            println("Trying to access index $currentIndex of profile $name with a max index of $totalPoints")
            point.isLastPoint = true
            return point
        }
        point.position = positions[currentIndex]
        point.velocity = speeds[currentIndex]
        point.timeDurMs = durations[currentIndex]
        point.isLastPoint = false
        if (currentIndex+1 == totalPoints)
            point.isLastPoint = true

        currentIndex++
        return point
    }
}