package org.team401.lib

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*

class DataLogger(val name: String, val push: Boolean) : Loop {

    private val data = HashMap<String, () -> Any>()
    private val date = Date()
    private val timestamp = "${date.month}-${date.date}-${date.hours}-${date.minutes}"

    init {
        register("Match Time", { FMS.getMatchTime() })
        register("Is Autonomous", { FMS.isAutonomous() })
    }

    override fun onStart() {
        val logFolder = File("/home/lvuser/logs/$name")
        if (!logFolder.exists())
            logFolder.mkdirs()

        PrintWriter(FileWriter("/home/lvuser/logs/$name-$timestamp.csv", true)).use { writer ->
            data.forEach { key, data -> writer.print("$key,") }
            writer.println()
        }
    }

    override fun onLoop() {
        if (FMS.isRobotEnabled()) {
            PrintWriter(FileWriter("/home/lvuser/logs/$name-$timestamp.csv", true)).use { writer ->
                data.forEach { key, data ->
                    try {
                        writer.print("${data()},")
                    } catch (t: Throwable) {
                        CrashTracker.logThrowableCrash(t)
                    }
                }
                if (data.isNotEmpty())
                    writer.println()
            }
        }
        data.forEach { key, data ->
            val d = data()
            if (push && isLowercase(key)) {
                if (d is Number)
                    SmartDashboard.putNumber(key, d.toDouble())
                else if (d is Boolean)
                    SmartDashboard.putBoolean(key, d)
                else
                    SmartDashboard.putString(key, d.toString())
            }
        }
    }

    fun register(name: String, obj: () -> Any) {
        data.put(name, obj)
    }

    override fun onStop() {

    }

    fun isLowercase(str: String) = str.toLowerCase() == str
}