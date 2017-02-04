package org.team401.robot.components

import org.strongback.components.Motor
import java.util.*

class CollectionGearbox(pro1: Motor, pro2: Motor, pro3: Motor) {

    val motors: List<Motor>

    init {
        motors = ArrayList()
        motors.add(pro1)
        motors.add(pro2)
        motors.add(pro3)
    }
}