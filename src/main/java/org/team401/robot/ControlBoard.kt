package org.team401.robot

import org.strongback.hardware.Hardware

object ControlBoard {

    private val left = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT)
    private val right = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT)
    private val mash = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.MASHER_JOYSTICK)

    // drive
    fun getDrivePitch() = left.pitch.read()
    fun getDriveStrafe() = left.roll.read()
    fun getDriveRotate() = right.roll.read()

    // controls left
    fun getShift() = left.getButton(Constants.BUTTON_SHIFT)
    fun getToggleHeading() = left.getButton(Constants.BUTTON_TOGGLE_HEADING)

    // controls right
    fun getToggleCamera() = right.getButton(Constants.BUTTON_SWITCH_CAMERA)
    fun getToggleGear() = right.getButton(Constants.BUTTON_GEAR)
    fun getIntakeDrop() = right.getButton(Constants.BUTTON_ARM_DROP)
    fun getToggleIntake() = right.getButton(Constants.BUTTON_TOGGLE_INTAKE)

    // controls turret
    fun getShootFuel() = mash.getButton(Constants.BUTTON_SHOOT_FUEL)
    fun getToggleSentry() = mash.getButton(Constants.BUTTON_TOGGLE_SENTRY)
    fun getToggleAuto() = mash.getButton(Constants.BUTTON_TOGGLE_AUTO)
    fun getToggleHood() = mash.getButton(Constants.BUTTON_TOGGLE_HOOD)
    fun getToggleTower() = mash.getButton(Constants.BUTTON_EXTEND_TOWER)

    fun getTurretYaw() = mash.yaw.read()
    fun getTurretThrottle() = mash.throttle.invert().read()

    fun getLeftDriveJoystick() = left
    fun getRightDriveJoystick() = right
    fun getMasherJoystick() = mash
}