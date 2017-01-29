package org.team401.robot.commands.camera

import org.strongback.command.Command
import org.team401.robot.camera.Camera

class UpdateCamera(val camera: Camera) : Command() {

    override fun execute(): Boolean {
        camera.getImage()
        return false
    }
}