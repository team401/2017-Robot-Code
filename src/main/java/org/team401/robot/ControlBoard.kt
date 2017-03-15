package org.team401.robot

import org.strongback.hardware.Hardware

object ControlBoard {

    private val drive = Hardware.HumanInterfaceDevices.logitechDualAction(Constants.DRIVER_GAMEPAD)
    private val mash = Hardware.HumanInterfaceDevices.logitechDualAction(Constants.MASHER_GAMEPAD)

    // drive
    fun getDrivePitch() = drive.getAxis(1).read()
    fun getDriveStrafe() = drive.getAxis(0).read()
    fun getDriveRotate() = drive.getAxis(2).read()

    // controls drive
    fun getShift() = drive.getButton(6)
    fun getToggleHeading() = drive.getButton(5)
    fun getGearOut() = drive.getButton(8)
    fun getGearIntake() = drive.getButton(7)
    fun getResetGyro() = drive.getButton(9)
    fun getGyroPadAngle() = drive.getDPad(0)

    // controls turret
    fun getShootFuel() = mash.getButton(8)
    fun getToggleSentry() = mash.getButton(3)
    fun getToggleAuto() = mash.getButton(1)
    fun getToggleHood() = mash.getButton(6)
    fun getToggleTower() = mash.getButton(2)
    fun getInverseHopper() = mash.getButton(5)
    fun getToggleIntake() = mash.getButton(7)
    fun getCalibrateTurret() = mash.getButton(10)
    fun getTurretSnapAngle() = mash.getDPad(0).direction
    fun getToggleCamera() = mash.getButton(4)

    fun getTurretYaw() = mash.getAxis(0).read()
    fun getTurretThrottle() = mash.getAxis(3).read()

    fun getDriveController() = drive
    fun getMasherController() = mash
}