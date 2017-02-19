package org.team401.robot.auto.actions

interface Action {

    fun start()

    fun update()

    fun isFinished(): Boolean

    fun end()
}