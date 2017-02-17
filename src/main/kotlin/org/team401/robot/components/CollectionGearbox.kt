package org.team401.robot.components

import org.strongback.components.Motor

class CollectionGearbox(pro1: Motor, pro2: Motor, pro3: Motor) {

    private val motor = Motor.compose(pro1, pro2, pro3)

    fun setSpeed(throttle: Double) = motor.setSpeed(throttle)

    fun isRunning() = motor.speed == 0.0

}