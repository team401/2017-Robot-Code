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

    private val writer = PrintWriter(FileWriter("/home/lvuser/logs/$name-$timestamp.csv", true))

    init {
        register("Match Time", { FMS.getMatchTime() })
        register("Is Autonomous", { FMS.isAutonomous() })
    }

    override fun onStart() {
        val logFolder = File("/home/lvuser/logs/")
        if (!logFolder.exists())
            logFolder.mkdirs()

        data.forEach { key, data -> writer.print("$key,") }
        writer.println()
    }

    override fun onLoop() {
        if (FMS.isRobotEnabled()) {
            data.forEach { key, data ->
                try {
                    val result = data()
                    writer.print("$result,")

                    if (push && isLowercase(key)) {
                        if (result is Number)
                            SmartDashboard.putNumber(key, result.toDouble())
                        else if (result is Boolean)
                            SmartDashboard.putBoolean(key, result)
                        else
                            SmartDashboard.putString(key, result.toString())
                    }
                } catch (t: Throwable) {
                    CrashTracker.logThrowableCrash(t)
                }
            }
            writer.println()
        }
    }

    fun register(name: String, obj: () -> Any) {
        data.put(name, obj)
    }

    override fun onStop() {

    }

    fun isLowercase(str: String) = str.toLowerCase() == str
}