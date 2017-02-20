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
    const val COMPRESSOR_FAN        = 5
    const val TURRET_LED_RING       = 6
    // solenoid ids (module 2)

    // can device ids
    const val PDP                   = 0
    const val PCM                   = 12
    // can device ids for the motor controllers
    const val FRONT_LEFT_MASTER     = 1
    const val FRONT_LEFT_SLAVE      = 2
    const val FRONT_RIGHT_MASTER    = 3
    const val FRONT_RIGHT_SLAVE     = 4
    const val REAR_LEFT_MASTER      = 6
    const val REAR_LEFT_SLAVE       = 5
    const val REAR_RIGHT_MASTER     = 7
    const val REAR_RIGHT_SLAVE      = 8
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
    const val BUTTON_SHIFT          = 0
    const val BUTTON_TOGGLE_GYRO    = 2
    // right drive joystick
    const val BUTTON_SWITCH_CAMERA  = 1
    const val BUTTON_COL_DROP       = 2
    const val BUTTON_COL_TOGGLE     = 3
    const val BUTTON_GEAR           = 0
    // masher joystick
    const val BUTTON_SHOOT_FUEL     = 0
    const val BUTTON_EXTEND_TURRET  = 1
    const val BUTTON_TOGGLE_SENTRY  = 3
    const val BUTTON_TOGGLE_AUTO    = 5
    const val BUTTON_TOGGLE_HOOD    = 2

    // camera mapping
    const val CAMERA_FRONT          = 0
    const val CAMERA_BACK           = 1

    // measurements
    const val DRIVE_WHEEL_DIAMETER_IN  = 4

    // pid
    const val SPEED_CONTROL_PROFILE = 0
    const val SPEED_P               = 0.0
    const val SPEED_I               = 0.0
    const val SPEED_D               = 0.0
    const val SPEED_F               = 0.238
    const val SPEED_IZONE           = 0
    const val SPEED_RAMP_RATE       = 0.0

    const val FLYWHEEL_P            = 1.0
    const val FLYWHEEL_I            = 0.0
    const val FLYWHEEL_D            = 0.0
    const val FLYWHEEL_F            = 0.0
    const val FLYWHEEL_IZONE        = 0
    const val FLYWHEEL_RAMP_RATE    = 0.0

    const val DRIVE_HEADING_VEL_P   = 4.0 // 6.0;
    const val DRIVE_HEADING_VEL_I   = 0.0
    const val DRIVE_HEADING_VEL_D   = 50.0

    const val LOOP_PERIOD           = 0.001

}