package org.team401.robot.auto

import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.team401.lib.FMS
import org.team401.robot.auto.modes.*

class AutoModeSelector {

	internal enum class StartingPos {
		LEFT, CENTER, RIGHT
	}

	private enum class Auto {
		GEAR, FUEL, GEAR_FUEL, NONE
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
		strategyChooser.addObject("None", Auto.NONE)
		SmartDashboard.putData("Strategy", strategyChooser)
	}

	fun getAutoMode(): AutoMode {
		when (strategyChooser.selected) {
			Auto.GEAR -> {
				if (positionChooser.selected == StartingPos.CENTER)
                    return CenterGear()
                else
                    return SideGear(positionChooser.selected)
			}
			Auto.FUEL -> {
				if ((positionChooser.selected == StartingPos.RIGHT && FMS.isBlueAlliance()) ||
						(positionChooser.selected == StartingPos.LEFT && FMS.isRedAlliance()))
                    return FarHopperFuel(positionChooser.selected)
                if (positionChooser.selected == StartingPos.CENTER)
                    return CenterGearAndFuel()
                return HopperFuel(positionChooser.selected)
			}
			Auto.GEAR_FUEL -> {
				if ((positionChooser.selected == StartingPos.RIGHT && FMS.isBlueAlliance()) ||
						(positionChooser.selected == StartingPos.LEFT && FMS.isRedAlliance())) {
					println("Bad fuel auto configuration!!!")
					return CalibrateTurret()
				}
                if (positionChooser.selected == StartingPos.CENTER)
                    return CenterGearAndFuel()
                return SideGearAndFuel(positionChooser.selected)
			}
			else -> return CalibrateTurret()
		}
	}
}