package org.team401.lib

import java.io.File
import java.util.*

object MotionProfileParser {

    /**
     * Parses a motion profile from a .csv file. Returns an empty profile if any error occurs.
     */
    fun parse(name: String, path: String): MotionProfile {
        val empty = MotionProfile(name, DoubleArray(0), DoubleArray(0), IntArray(0))
        val file = File(path)
        val lines = try {
            file.readLines()
        } catch (e: Exception) {
            println("Could not find motion profile $name")
            CrashTracker.logThrowableCrash(e)
            return empty
        }

        val positions = ArrayList<Double>()
        val speeds = ArrayList<Double>()
        val durations = ArrayList<Int>()

        try {
            lines.forEach {
                val entries = it.substring(1, it.length - 3).split(",")
                positions.add(entries[0].toDouble())
                speeds.add(entries[1].toDouble())
                durations.add((entries[2].toDouble() + .5).toInt())
            }
        } catch (e: Exception) {
            print("Could not parse motion profile $path")
            CrashTracker.logThrowableCrash(e)
            return empty
        }

        return MotionProfile(name, positions.toDoubleArray(), speeds.toDoubleArray(), durations.toIntArray())
    }
}