package org.team401.lib

import java.io.File
import java.util.*

object MotionProfileParser {

    fun parse(name: String, path: String): MotionProfile {
        val file = File(path)
        val lines = file.readLines()

        val positions = ArrayList<Double>()
        val speeds = ArrayList<Double>()
        val durations = ArrayList<Int>()

        lines.forEach {
            val entries = it.split(",")
            positions.add(entries[0].toDouble())
            speeds.add(entries[1].toDouble())
            durations.add((entries[2].toDouble()+.5).toInt())
        }

        return MotionProfile(name, positions.toTypedArray(), speeds.toTypedArray(), durations.toTypedArray())
    }
}