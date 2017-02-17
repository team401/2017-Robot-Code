package org.team401.robot

object Constants {
    // gearbox index values for the chassis
    const val GEARBOX_FRONT_LEFT    = 0
    const val GEARBOX_FRONT_RIGHT   = 1
    const val GEARBOX_REAR_LEFT     = 2
    const val GEARBOX_REAR_RIGHT    = 3

    // solenoid ids (module 1)
    const val GEARBOX_SHIFTER       = 0
    const val TURRET_HOOD           = 1
    const val COL_EXTENDER          = 2
    const val GEAR_HOLDER           = 3
    const val TURRET_SHIFTER        = 4
    // solenoid ids (module 2)

    // can device ids
    const val PDP                   = 0
    const val PCM                   = 12
    // can device ids for the motor controllers
    // CIM is master, PRO is slave
    const val CIM_FRONT_LEFT        = 5
    const val PRO_FRONT_LEFT        = 6
    const val CIM_FRONT_RIGHT       = 1
    const val PRO_FRONT_RIGHT       = 2
    const val CIM_REAR_LEFT         = 7
    const val PRO_REAR_LEFT         = 8
    const val CIM_REAR_RIGHT        = 3
    const val PRO_REAR_RIGHT        = 4
    // can device ids for the turret
    const val TURRET_SHOOTER_LEFT   = 9
    const val TURRET_SHOOTER_RIGHT  = 10
    const val TURRET_ROTATOR        = 11
    const val TURRET_FEEDER         = 12

    // pwm motor ports
    const val COL_PRO_1             = 1
    const val COL_PRO_2             = 2
    const val COL_PRO_3             = 3

    // joystick ids
    const val DRIVE_JOYSTICK_LEFT   = 0
    const val DRIVE_JOYSTICK_RIGHT  = 1
    const val MASHER_JOYSTICK       = 2

    // left drive joystick
    const val BUTTON_SHIFT          = 2
    const val BUTTON_SWITCH_CAMERA  = 1
    // right drive joystick
    const val BUTTON_COL_DROP       = 2
    const val BUTTON_COL_TOGGLE     = 3
    const val BUTTON_GEAR           = 0
    // masher joystick
    const val BUTTON_SHOOT_FUEL     = 0
    const val BUTTON_EXTEND_TURRET  = 1
    const val BUTTON_DISABLE_SENTRY = 2
    const val BUTTON_TOGGLE_HOOD    = 3


    // camera mapping
    const val CAMERA_FRONT          = 0
    const val CAMERA_BACK           = 1

}