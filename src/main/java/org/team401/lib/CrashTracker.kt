package org.team401.lib

import java.io.PrintWriter
import java.io.IOException
import java.io.FileWriter
import java.util.UUID
import java.util.Date

object CrashTracker {

	private val RUN_INSTANCE_UUID = UUID.randomUUID()

	fun logRobotStartup() {
		logMarker("robot startup")
	}

	fun logRobotConstruction() {
		logMarker("robot startup")
	}

	fun logRobotInit() {
		logMarker("robot init")
	}

	fun logTeleopInit() {
		logMarker("teleop init")
	}

	fun logAutoInit() {
		logMarker("auto init")
	}

	fun logDisabledInit() {
		logMarker("disabled init")
	}

	fun logThrowableCrash(throwable: Throwable) {
		logMarker("Exception", throwable)
	}

	private fun logMarker(mark: String) {
		logMarker(mark, null)
	}

	private fun logMarker(mark: String, nullableException: Throwable?) {
		try {
			PrintWriter(FileWriter("/home/lvuser/crash_tracking.txt", true)).use { writer ->
				writer.print(RUN_INSTANCE_UUID.toString())
				writer.print(", ")
				writer.print(mark)
				writer.print(", ")
				writer.print(Date().toString())

				if (nullableException != null) {
					writer.print(", ")
					nullableException.printStackTrace(writer)
					println(nullableException.message)
				}

				writer.println()
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}
}