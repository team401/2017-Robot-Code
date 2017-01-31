package org.team401.robot.camera

import edu.wpi.cscore.UsbCamera
import edu.wpi.first.wpilibj.CameraServer
import org.team401.robot.Constants

class Camera {

    val server = CameraServer.getInstance()!!

    val frontCam: UsbCamera
    val backCam: UsbCamera

    var frontEnabled: Boolean = true

    init {
        frontCam = server.startAutomaticCapture("Front", Constants.CAMERA_FRONT)
        frontCam.setResolution(480, 400)
        frontCam.setFPS(10)
        backCam = server.startAutomaticCapture("Back", Constants.CAMERA_BACK)
        backCam.setResolution(480, 400)
        backCam.setFPS(10)
    }

    fun getCurrentCamera(): UsbCamera {
        if (frontEnabled)
            return frontCam
        return backCam
    }

    fun switchCamera() {
        if (frontEnabled)
            switchToBackCamera()
        else
            switchToFrontCamera()
    }

    fun switchToBackCamera() {
        frontEnabled = false
        server.server.source = backCam
    }

    fun switchToFrontCamera() {
        frontEnabled = true
        server.server.source = frontCam
    }
}