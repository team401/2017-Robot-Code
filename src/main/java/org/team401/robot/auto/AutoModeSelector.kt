package org.team401.robot.auto

import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.robot.auto.modes.*

class AutoModeSelector {

    private enum class StartingPos {
        LEFT, CENTER, RIGHT
    }

    private enum class Auto {
        GEAR, FUEL, GEAR_FUEL
    }

    private val positionChooser = SendableChooser<StartingPos>()
    private val strategyChooser = SendableChooser<Auto>()

    init {
        positionChooser.addObject("Station 1", StartingPos.LEFT)
        positionChooser.addObject("Station 2", StartingPos.CENTER)
        positionChooser.addObject("Station 3", StartingPos.RIGHT)
        SmartDashboard.putData("Starting Position", positionChooser)

        strategyChooser.addObject("Gear Only", Auto.GEAR)
        strategyChooser.addObject("Fuel Only", Auto.FUEL)
        strategyChooser.addObject("Gear then Fuel", Auto.GEAR_FUEL)
        SmartDashboard.putData("Strategy", strategyChooser)
    }

    fun getAutoMode(): AutoMode {
        when (strategyChooser.selected) {
            Auto.GEAR -> {
                when (positionChooser.selected) {
                    StartingPos.LEFT -> return LeftGear()
                    StartingPos.CENTER -> return ForwardGear()
                    StartingPos.RIGHT -> return RightGear()
                    else -> return CalibrateTurret()
                }
            }
            Auto.FUEL -> {
                if ((positionChooser.selected != StartingPos.RIGHT && HAL.getAllianceStation().ordinal < 3) ||
                        (positionChooser.selected != StartingPos.LEFT && HAL.getAllianceStation().ordinal >= 3)) {
                    println("Bad fuel auto configuration!!!")
                    return CalibrateTurret()
                } else {
                    if (HAL.getAllianceStation().ordinal >= 3)
                        return LeftFuel()
                    else
                        return RightFuel()
                }
            }
            Auto.GEAR_FUEL -> {
                if ((positionChooser.selected != StartingPos.RIGHT && HAL.getAllianceStation().ordinal < 3) ||
                        (positionChooser.selected != StartingPos.LEFT && HAL.getAllianceStation().ordinal >= 3)) {
                    println("Bad fuel auto configuration!!!")
                    return CalibrateTurret()
                } else {
                    if (HAL.getAllianceStation().ordinal >= 3)
                        return LeftGearAndFuel()
                    else
                        return RightGearAndFuel()
                }
            }
            else -> return CalibrateTurret()
        }
    }
}