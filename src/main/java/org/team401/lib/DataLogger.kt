package org.team401.lib

import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

object DataLogger : Thread() {

    private val RUN_INSTANCE_UUID = UUID.randomUUID()
    private val map = HashMap<String, DataObject>()

    fun addLogger(key: String, data: DataObject) = map.put(key, data)

    override fun run() {
        while (true) {
            try {
                PrintWriter(FileWriter("/home/lvuser/crash_tracking.txt", true)).use { writer ->
                    writer.print(RUN_INSTANCE_UUID.toString())
                    map.forEach { k, o -> writer.print(" $k: ${o.getData()}, ") }
                    writer.print(Date().toString())

                    writer.println()
                }
                Thread.sleep(750)
            } catch (t: Throwable) {
                CrashTracker.logThrowableCrash(t)
            }
        }
    }

    interface DataObject {
        fun getData(): Any
    }
}