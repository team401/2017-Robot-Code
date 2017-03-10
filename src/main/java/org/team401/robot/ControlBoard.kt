package org.team401.robot

import org.strongback.hardware.Hardware

object ControlBoard {

    private val left = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_LEFT)
    private val right = Hardware.HumanInterfaceDevices.logitechAttack3D(Constants.DRIVE_JOYSTICK_RIGHT)
    private val mash = Hardware.HumanInterfaceDevices.logitechDualAction(Constants.MASHER_JOYSTICK)

    // drive
    fun getDrivePitch() = left.pitch.read()
    fun getDriveStrafe() = left.roll.read()
    fun getDriveRotate() = right.roll.read()

    // controls left
    fun getShift() = left.getButton(Constants.BUTTON_SHIFT)
    fun getToggleHeading() = left.getButton(Constants.BUTTON_TOGGLE_HEADING)
    fun getToggleCamera() = left.getButton(Constants.BUTTON_TOGGLE_CAMERA)

    // controls right
    fun getToggleGear() = right.getButton(Constants.BUTTON_GEAR)
    fun getIntakeDrop() = right.getButton(Constants.BUTTON_ARM_DROP)

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

    fun getLeftDriveJoystick() = left
    fun getRightDriveJoystick() = right
    fun getMasherGamepad() = mash
}