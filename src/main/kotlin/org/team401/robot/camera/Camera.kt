package org.team401.robot.camera

import edu.wpi.cscore.CvSink
import edu.wpi.cscore.UsbCamera
import edu.wpi.first.wpilibj.CameraServer
import org.team401.robot.Constants

class Camera(val width: Int, val height: Int, val fps: Int) {

    val cameraServer = CameraServer.getInstance()!!

    val frontCam: UsbCamera
    val frontSink: CvSink
    val backCam: UsbCamera
    val backSink: CvSink

    var frontEnabled: Boolean = true

    init {
        frontCam = cameraServer.startAutomaticCapture("Front", Constants.CAMERA_FRONT)
        frontCam.setResolution(width, height)
        frontCam.setFPS(fps)
        frontSink = cameraServer.getVideo(frontCam)
        frontSink.setEnabled(true)
        backCam = cameraServer.startAutomaticCapture("Back", Constants.CAMERA_BACK)
        backCam.setResolution(width, height)
        backCam.setFPS(fps)
        backSink = cameraServer.getVideo(backCam)
        backSink.setEnabled(true)
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
        cameraServer.server.source = backCam
    }

    fun switchToFrontCamera() {
        frontEnabled = true
        cameraServer.server.source = frontCam
    }
}