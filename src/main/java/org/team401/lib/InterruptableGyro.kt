package org.team401.lib

import edu.wpi.first.wpilibj.interfaces.Gyro

interface InterruptableGyro : Gyro {

    fun startCalibrate()

    fun endCalibrate()

    fun cancelCalibrate()

    fun getCenter(): Double
}