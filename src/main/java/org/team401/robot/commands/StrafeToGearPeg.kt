package org.team401.robot.commands

import com.ctre.CANTalon
import edu.wpi.first.wpilibj.PIDController
import edu.wpi.first.wpilibj.PIDSource
import edu.wpi.first.wpilibj.PIDSourceType
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.strongback.command.Command
import org.team401.robot.subsystems.OctocanumDrive
import org.team401.lib.DistanceSensor
import org.team401.vision.VisionDataStream.VisionData
import org.team401.vision.VisionDataStream.VisionDataStream

class StrafeToGearPeg(val octocanumDrive: OctocanumDrive, val stream: VisionDataStream) : Command() {

    val controller: PIDController
    var fieldCentric: Boolean = false

    init {
        controller = PIDController(1.0, 0.0, 0.0, StrafeError(stream)) {}
        controller.enable()
    }

    override fun initialize() {
        octocanumDrive.changeControlMode(CANTalon.TalonControlMode.PercentVbus,
                { it.set(0.0) },
                { it.set(0.0) },
                { it.set(0.0) },
                { it.set(0.0) })
        fieldCentric = SmartDashboard.getBoolean("Field-Centric", false)
        SmartDashboard.putBoolean("Field-Centric", false)
    }

    override fun execute(): Boolean {
        val latestData = stream.latestGearData

        if (!latestData.isValid || (latestData.distance < 5 && latestData.strafe == VisionData.Strafe.CENTER && latestData.yaw < 2))
            return true

        var strafe = 0.0
        if (latestData.strafe == VisionData.Strafe.LEFT)
            strafe = -.4
        else if (latestData.strafe == VisionData.Strafe.RIGHT)
            strafe = .4

        val rotation = latestData.yaw/90

        var forward = 0.0
        if (latestData.yaw < 2)
            forward = latestData.distance*.035
        if (forward > .75)
            forward = .75


        octocanumDrive.drive(forward, strafe, rotation)
        return false
    }

    class StrafeError(val stream: VisionDataStream) : PIDSource {

        override fun getPIDSourceType(): PIDSourceType {
            return PIDSourceType.kDisplacement
        }

        override fun setPIDSourceType(pidSource: PIDSourceType) {}

        override fun pidGet(): Double {
            return stream.latestGearData.yaw
        }

    }

    override fun interrupted() {
        end()
    }

    override fun end() {
        SmartDashboard.putBoolean("Field-Centric", fieldCentric)
        controller.disable()
    }

}