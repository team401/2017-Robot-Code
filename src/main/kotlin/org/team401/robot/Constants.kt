package org.team401.robot

object Constants {
    // gearbox index values for the chassis and shifter
    const val GEARBOX_FRONT_LEFT    = 0
    const val GEARBOX_FRONT_RIGHT   = 1
    const val GEARBOX_REAR_LEFT     = 2
    const val GEARBOX_REAR_RIGHT    = 3
    const val SHIFTER_EXTEND        = 0
    const val SHIFTER_RETRACT       = 1

    // can device ids
    const val PDP                   = 0
    const val PCM                   = 1
    // can device ids for the motor controllers
    const val CIM_FRONT_LEFT        = 2
    const val PRO_FRONT_LEFT        = 3
    const val CIM_FRONT_RIGHT       = 4
    const val PRO_FRONT_RIGHT       = 5
    const val CIM_REAR_LEFT         = 6
    const val PRO_REAR_LEFT         = 7
    const val CIM_REAR_RIGHT        = 8
    const val PRO_REAR_RIGHT        = 9
    // can device ids for the climbing mechanism
    const val CLIMBING_LEFT         = 10
    const val CLIMBING_RIGHT        = 11
    // can device ids for the turret
    const val SHOOTER_CONTROLLER    = 12
    const val FEEDER_CONTROLLER     = 13
    const val HOOD_CONTROLLER       = 14
    const val SPIN_CONTROLLER       = 15
    // can device ids for intake
    const val INTAKE_1              = 16
    const val INTAKE_2              = 17
    // can device ids for the ball agitator
    const val HOPPER_AGITATOR       = 18

}