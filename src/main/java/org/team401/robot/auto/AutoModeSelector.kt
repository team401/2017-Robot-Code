package org.team401.robot.auto

import edu.wpi.first.wpilibj.Sendable
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
    private val hopperChooser = SendableChooser<Int>()

	private val motionProfile = SendableChooser<Boolean>()

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

        hopperChooser.addObject("Near", 0)
        hopperChooser.addObject("Far", 1)
        SmartDashboard.putData("Hopper Chooser", hopperChooser)

		//code to run auto off motion profiles
		//WARNING: EXPERIMENTAL
		motionProfile.addDefault("Off", false)
		motionProfile.addObject("On", true)
		SmartDashboard.putData("Motion Profile Auto", motionProfile)
	}

	fun getAutoMode(): AutoMode {
		//if motionProfiles are off
		if (motionProfile.selected == false) {

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
                        return FarHopperFuel(positionChooser.selected, hopperChooser.selected == 1)
                    if (positionChooser.selected == StartingPos.CENTER)
                        return CenterGearAndFuel()
                    return HopperFuel(positionChooser.selected, hopperChooser.selected == 1)
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
        }else{
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
                        return FarHopperFuel(positionChooser.selected, hopperChooser.selected == 1)
                    if (positionChooser.selected == StartingPos.CENTER)
                        return CenterGearAndFuel()
                    return HopperFuel(positionChooser.selected, hopperChooser.selected == 1)
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
		return CalibrateTurret()
	}
}