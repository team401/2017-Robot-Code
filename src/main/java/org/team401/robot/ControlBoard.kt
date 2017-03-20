package org.team401.robot

import org.strongback.components.Switch
import org.strongback.hardware.Hardware

object ControlBoard {

    private val drive = Hardware.HumanInterfaceDevices.logitechDualAction(Constants.DRIVER_GAMEPAD)
    private val mash = Hardware.HumanInterfaceDevices.logitechF310(Constants.MASHER_GAMEPAD)

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
    fun getToggleSentry() = mash.getButton(2)
    fun getToggleAuto() = mash.getButton(3)
    fun getToggleHood() = mash.getButton(6)
    fun getToggleTower() = mash.getButton(1)
    fun getInverseHopper() = mash.getButton(5)
    fun getCalibrateTurret() = mash.getButton(9)
    fun getToggleCamera() = mash.getButton(4)
    fun getTurretSnapLeft() = mash.getButton(13)
    fun getTurretSnapCenter() = mash.getButton(11)
    fun getTurretSnapRight() = mash.getButton(14)
    fun getIntakeThrottle() = mash.getAxis(4).read()
    fun getToggleIntake() = Switch { mash.getAxis(4).read() > .25 }
    fun getShootFuel() = Switch { mash.getAxis(5).read() > .25 }

    fun getTurretYaw() = mash.getAxis(0).read()
    fun getTurretThrottle() = mash.getAxis(3).read()

    fun getDriveController() = drive
    fun getMasherController() = mash
}