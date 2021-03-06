package org.team401.robot

object Constants {

    // measurements
    const val DRIVE_WHEEL_DIAMETER_IN  = 4
    const val TURRET_GEAR_MULTIPLIER = 18.0/168.0

    // gearbox index values for the chassis
    const val GEARBOX_FRONT_LEFT    = 0
    const val GEARBOX_FRONT_RIGHT   = 1
    const val GEARBOX_REAR_LEFT     = 2
    const val GEARBOX_REAR_RIGHT    = 3

    // solenoid ids (module 1)
    const val GEARBOX_SHIFTER       = 0
    const val TURRET_HOOD           = 1
    const val ARM_EXTENDER          = 2
    const val GEAR_HOLDER           = 4
    const val TOWER_SHIFTER         = 3
    const val COMPRESSOR_FAN        = 5
    const val GEAR_LED_RING         = 6
    const val TURRET_LED_RING       = 7

    // can device ids for the motor controllers
    const val FRONT_LEFT_MASTER     = 1
    const val FRONT_LEFT_SLAVE      = 2
    const val FRONT_RIGHT_MASTER    = 3
    const val FRONT_RIGHT_SLAVE     = 4
    const val REAR_LEFT_MASTER      = 5
    const val REAR_LEFT_SLAVE       = 6
    const val REAR_RIGHT_MASTER     = 7
    const val REAR_RIGHT_SLAVE      = 8
    // can device ids for the turret
    const val TURRET_FLYWHEEL_SLAVE = 9
    const val TURRET_FLYWHEEL_MASTER = 10
    const val TURRET_FEEDER         = 11
    const val TURRET_ROTATOR        = 12

    // pwm motor ports
    const val INTAKE_1              = 9
    const val INTAKE_2              = 7
    const val HOPPER_BOTTOM         = 8

    // servos
    const val SERVO_LEFT            = 0
    const val SERVO_RIGHT           = 1

    const val HOPPER_RAMP_RATE      = 6

    // joystick ids
    const val DRIVER_GAMEPAD        = 0
    const val MASHER_GAMEPAD        = 1

    // camera mapping
    const val CAMERA_FRONT          = 0
    const val CAMERA_BACK           = 1

    // pid
    const val SPEED_CONTROL_PROFILE = 0
    const val SPEED_P               = 0.6
    const val SPEED_I               = 0.0
    const val SPEED_D               = 1.0
    const val SPEED_F               = 0.0
    const val SPEED_IZONE           = 0
    const val SPEED_RAMP_RATE       = 0.0
    const val MAX_SPEED             = 13.0

    const val FLYWHEEL_P            = 0.1//0.09
    const val FLYWHEEL_I            = 0.0//0.00005
    const val FLYWHEEL_D            = 0.0
    const val FLYWHEEL_F            = 0.0275//0.02899
    const val FLYWHEEL_IZONE        = 0//20
    const val FLYWHEEL_RAMP_RATE    = 0.0

    const val ROTATOR_P             = 0.07
    const val ROTATOR_I             = 0.0
    const val ROTATOR_D             = 20.0
    const val ROTATOR_F             = 0.0
    const val ROTATOR_IZONE         = 0
    const val ROTATOR_RAMP_RATE     = 0.0

    const val DRIVE_HEADING_VEL_P   = 1.5
    const val DRIVE_HEADING_VEL_I   = 0.0
    const val DRIVE_HEADING_VEL_D   = 0.0

    const val LOOP_PERIOD           = 1.0 / 100
    const val ACTION_PERIOD         = 1.0 / 100

}