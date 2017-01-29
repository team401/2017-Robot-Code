package org.team401.robot.camera

import edu.wpi.cscore.UsbCamera
import edu.wpi.first.wpilibj.CameraServer
import org.strongback.Strongback
import org.team401.robot.commands.camera.UpdateCamera

class Camera {

    val server = CameraServer.getInstance()!!

    val frontCam: UsbCamera
    val backCam: UsbCamera

    var frontEnabled: Boolean = true

    init {
        frontCam = server.startAutomaticCapture("Front", 0)
        backCam = server.startAutomaticCapture("Back", 1)

        Strongback.submit(UpdateCamera(this))
    }

    fun getCurrentCamera(): UsbCamera {
        if (frontEnabled)
            return frontCam
        return backCam
    }

    fun getImage() {
        server.putVideo("DashCam", 640, 480)
    }

    fun switchCamera() {
        frontEnabled = !frontEnabled
    }

    fun switchToBackCamera() {
        frontEnabled = false
    }

    fun switchToFrontCamera() {
        frontEnabled = true
    }
}