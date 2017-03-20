package org.team401.robot.loops

import edu.wpi.first.wpilibj.Timer
import org.team401.robot.subsystems.OctocanumDrive

import org.team401.lib.ADXRS450_Gyro
import org.team401.lib.Loop


class GyroCalibrator : Loop {

    val gyro = OctocanumDrive.gyro
    var calibrationStartTime = 0.0
    
    override fun onStart() {

    }

    override fun onLoop() {
        val now = Timer.getFPGATimestamp()
        // Keep re-calibrating the gyro every 5 seconds
        if (now - calibrationStartTime > ADXRS450_Gyro.kCalibrationSampleTime) {
            gyro.endCalibrate()
            System.out.println("Gyro calibrated, new zero is " + gyro.center)
            calibrationStartTime = now
            gyro.startCalibrate()
        }
    }

    override fun onStop() {
        gyro.cancelCalibrate()
    }
}