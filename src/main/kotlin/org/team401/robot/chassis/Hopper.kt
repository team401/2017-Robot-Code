package org.team401.robot.chassis

import org.strongback.components.Motor

class Hopper(val intakeMotorLeft: Motor, val intakeMotorRight: Motor, val agitatorMotor: Motor) {

    companion object {
        const val INTAKE_SPEED = 0.5
        const val AGITATOR_SPEED = 0.5
    }

    private var intakeEnabled = true
    private var agitatorEnabled = true

    fun toggleIntake() = enableIntake(!intakeEnabled)

    fun toggleAgitator() = enableAgitator(!agitatorEnabled)

    fun enableIntake(enabled: Boolean) {
        intakeEnabled = enabled
        setMotorModes()
    }

    fun enableAgitator(enabled: Boolean) {
        agitatorEnabled = enabled
        setMotorModes()
    }

    private fun setMotorModes() {
        if (intakeEnabled) {
            intakeMotorLeft.speed = INTAKE_SPEED
            intakeMotorRight.speed = INTAKE_SPEED
        } else {
            intakeMotorLeft.speed = 0.0
            intakeMotorRight.speed = 0.0
        }

        if (agitatorEnabled)
            agitatorMotor.speed = AGITATOR_SPEED
        else
            agitatorMotor.speed = 0.0
    }

}