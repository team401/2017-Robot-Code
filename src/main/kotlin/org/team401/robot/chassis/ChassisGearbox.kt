package org.team401.robot.chassis

import com.ctre.CANTalon
import org.strongback.components.Solenoid

class ChassisGearbox(val cimMotor: CANTalon, val littleMotor: CANTalon, val shifter: Solenoid) {

    var driveMode: DriveMode

    init {
        cimMotor.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)

        littleMotor.setControlMode(CANTalon.TalonControlMode.PercentVbus.value)

        shifter.retract()
        driveMode = DriveMode.DIRECT
    }

    fun shift() {
        if (driveMode == DriveMode.DIRECT)
            shift(DriveMode.MECHANUM)
        else
            shift(DriveMode.DIRECT)
    }

    fun shift(driveMode: DriveMode) {
        if (driveMode == DriveMode.DIRECT)
            shifter.retract()
        else
            shifter.extend()
    }

    enum class DriveMode {
        DIRECT,
        MECHANUM
    }
}