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
    // solenoid ids (module 2)

    // can device ids
    const val PDP                   = 0
    const val PCM1                  = 1
    const val PCM2                  = 2
    // can device ids for the motor controllers
    const val CIM_FRONT_LEFT        = 3
    const val PRO_FRONT_LEFT        = 4
    const val CIM_FRONT_RIGHT       = 5
    const val PRO_FRONT_RIGHT       = 6
    const val CIM_REAR_LEFT         = 7
    const val PRO_REAR_LEFT         = 8
    const val CIM_REAR_RIGHT        = 9
    const val PRO_REAR_RIGHT        = 10
    // can device ids for the turret
    const val TURRET_SHOOTER_LEFT   = 11
    const val TURRET_SHOOTER_RIGHT  = 12
    const val TURRET_ROTATOR        = 13

    // pwm motor ports
    const val TURRET_FEEDER         = 0
    const val COL_PRO_1             = 1
    const val COL_PRO_2             = 2
    const val COL_PRO_3             = 3

    // joystick ids
    const val DRIVE_JOYSTICK_LEFT   = 0
    const val DRIVE_JOYSTICK_RIGHT  = 1
    const val MASHER_JOYSTICK       = 2

    // joystick button mapping
    const val BUTTON_SHIFT          = 2
    const val BUTTON_SWITCH_CAMERA  = 1

    // camera mapping
    const val CAMERA_FRONT          = 0
    const val CAMERA_BACK           = 1

}