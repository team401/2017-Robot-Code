package org.team401.robot.loops

interface Loop {
    fun onStart()

    fun onLoop()

    fun onStop()
}