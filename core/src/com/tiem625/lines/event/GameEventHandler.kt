package com.tiem625.lines.event

abstract class GameEventHandler {

    abstract fun handle(event: GameEvent)
}