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
    fun getToggleCamera() = drive.getButton(3)
    fun getToggleGear() = drive.getButton(8)
    fun getResetGyro() = drive.getButton(9)

    // controls turret
    fun getShootFuel() = mash.getButton(Constants.BUTTON_SHOOT_FUEL)
    fun getToggleSentry() = mash.getButton(Constants.BUTTON_TOGGLE_SENTRY)
    fun getToggleAuto() = mash.getButton(Constants.BUTTON_TOGGLE_AUTO)
    fun getToggleHood() = mash.getButton(Constants.BUTTON_TOGGLE_HOOD)
    fun getToggleTower() = mash.getButton(Constants.BUTTON_EXTEND_TOWER)
    fun getHopper() = mash.getButton(Constants.BUTTON_HOPPER)
    fun getInverseHopper() = mash.getButton(Constants.BUTTON_INVERSE_HOPPER)
    fun getToggleIntake() = mash.getButton(Constants.BUTTON_TOGGLE_INTAKE)
    fun getClimb() = mash.getButton(Constants.BUTTON_CLIMB)
    fun getCalibrateTurret() = mash.getButton(Constants.BUTTON_CALIBRATE_TURRET)

    fun getTurretYaw() = mash.getAxis(Constants.AXIS_TURRET_ROTATE).read()
    fun getTurretThrottle() = mash.getAxis(Constants.AXIS_TURRET_THROTTLE).read()
}