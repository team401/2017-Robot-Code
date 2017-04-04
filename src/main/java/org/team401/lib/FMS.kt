package org.team401.lib

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.hal.HAL

/**
 * An object that's used to interface with the FMS, some features include
 *  - Alliance Color
 *  - Alliance Station Position
 *  - Game Time (counting down from 2:30)
 *  and much more...
 */
object FMS {

    enum class Alliance {
        RED, BLUE
    }

    enum class AllianceStation {
        STATION1, STATION2, STATION3
    }

    fun getAlliance(): Alliance {
        val hal = HAL.getAllianceStation()
        if (hal.ordinal < 3)
            return Alliance.RED
        return Alliance.BLUE
    }

    fun isBlueAlliance() = getAlliance() == Alliance.BLUE

    fun isRedAlliance() = getAlliance() == Alliance.RED

    fun getAllianceStation(): AllianceStation {
        val hal = HAL.getAllianceStation()
        if (hal.ordinal == 0 || hal.ordinal == 3)
            return AllianceStation.STATION1
        else if (hal.ordinal == 1 || hal.ordinal == 4)
            return AllianceStation.STATION2
        return AllianceStation.STATION3
    }

    fun getMatchTime(): Int {
        return DriverStation.getInstance().matchTime.toInt()
    }

    fun isRobotEnabled() = DriverStation.getInstance().isEnabled

    fun isAutonomous() = DriverStation.getInstance().isAutonomous
}