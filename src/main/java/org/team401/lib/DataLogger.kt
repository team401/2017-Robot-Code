package org.team401.lib

import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

class DataLogger(val name: String) : Loop {

    val data = ArrayList<() -> Any>()

    override fun onLoop() {
        PrintWriter(FileWriter("/home/lvuser/$name.txt", true)).use { writer ->
            data.forEach { writer.print("${it()},") }
            writer.println()
        }
    }

    fun register(obj: () -> Any) {
        data.add(obj)
    }

    override fun onStart() {

    }

    override fun onStop() {

    }
}