package org.team401.robot.commands

import com.ctre.CANTalon
import org.strongback.command.Command
import org.team401.robot.chassis.OctocanumDrive
import java.security.InvalidParameterException

/**
 * Drive each gearbox a certain number of rotations.
 *
 * @param octocanumDrive Reference to the drive system
 * @param mode The mode to drive in (Mecanum vs Traction)
 * @param rotations Array of length 4 with number of rotations for each motor.
 */
class DriveRobot(val octocanumDrive: OctocanumDrive, val mode: OctocanumDrive.DriveMode,
                 val rotations: DoubleArray, val tolarance: Double) : Command() {

    override fun initialize() {
        if (rotations.size != 4)
            throw InvalidParameterException("Invalid array size of ${rotations.size} (expected 4)")
        octocanumDrive.shift(mode)
        octocanumDrive.changeControlMode(CANTalon.TalonControlMode.Position,
                {// config left gearboxes pidf values
                    // it references a CANTalon object
                    it.p = 1.0
                    it.i = 0.0
                    it.d = 0.0
                    it.f = 0.0
                    it.position = 0.0
                    it.set(0.0)
                },
                {// config right gearboxes pidf values
                    // it references a CANTalon object
                    it.p = 1.0
                    it.i = 0.0
                    it.d = 0.0
                    it.f = 0.0
                    it.position = 0.0
                    it.set(0.0)
                })
    }

    override fun execute(): Boolean {
        return octocanumDrive.gearboxes.indices.filter {
            // check if each motor has traveled the right distance
            Math.abs(octocanumDrive.gearboxes[it].cimMotor.get() - rotations[it]) < tolarance
        }.size == 4
        // ^^^^^ return if all 4 are finished
    }

    override fun interrupted() {
        end()
    }

    override fun end() {
        octocanumDrive.changeControlMode(CANTalon.TalonControlMode.PercentVbus, { it.set(0.0) }, { it.set(0.0) })
    }
}