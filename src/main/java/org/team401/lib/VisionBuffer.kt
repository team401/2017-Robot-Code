package org.team401.lib

import org.team401.vision.VisionDataStream.VisionDataStream
import org.team401.vision.controller.VisionController
import java.util.*

object VisionBuffer {

    private val data = VisionDataStream("10.4.1.17", 5801)
    private val controller = VisionController("10.4.1.17", 5803)

    private val buffers = ArrayList<Buffer>()

    private val loop = object : Loop {

        override fun onStart() {
            buffers.forEach { it.reset() }
        }

        override fun onLoop() {
            buffers.forEach { it.update() }
        }

        override fun onStop() {

        }
    }

    init {
        data.start()
        controller.start()

        buffers.add(Buffer("goal_distance") { data.latestGoalDistance })
        buffers.add(Buffer("goal_yaw") { data.latestGoalYaw })
    }

    fun isLatestGoalValid() = data.isLatestGoalValid

    fun isLatestGearValid() = data.isLatestGearValid

    fun setGoalCameraMode(mode: VisionController.CameraMode) {
        controller.setCameraMode(VisionController.Camera.GOAL, mode)
    }

    fun setGearCameraMode(mode: VisionController.CameraMode) {
        controller.setCameraMode(VisionController.Camera.GEAR, mode)
    }

    fun toggleActiveCamera() {
        controller.toggleActiveCamera()
    }

    fun goalDistance() = findBuffer("goal_distance").getMedian()

    fun goalYaw() = data.latestGoalYaw //findBuffer("goal_yaw").getAverage()

    fun getBufferLoop() = loop

    private class Buffer(val name: String, val func: () -> Double) {

        val data = DoubleArray(20)

        fun update() {
            shift(func())
        }

        fun getAverage(): Double {
            var total = 0.0
            var delta = 0
            for (i in data) {
                if (i != 0.0) {
                    total += i
                    delta += 1
                }
            }

            return total / delta
        }

        fun getMedian(): Double {
            return data.sortedArray()[data.size/2]
        }

        private fun shift(new: Double) {
            for (i in 8 downTo 0)
                data[i+1] = data[i]
            data[0] = new
        }

        fun reset() {
            for (i in data.indices)
                data[i] = 0.0
        }
    }

    private fun findBuffer(name: String) = buffers.filter { it.name == "goal_distance" }.first()
}