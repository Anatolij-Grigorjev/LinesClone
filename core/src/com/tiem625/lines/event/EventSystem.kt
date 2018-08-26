package com.tiem625.lines.event

object EventSystem {

    val eventHandlers = mapOf(
            *GameEventTypes.values().map { it to mutableListOf<GameEventHandler>() }.toTypedArray()
    )

    fun addHandler(eventTypes: GameEventTypes, handler: GameEventHandler) {

        eventHandlers[eventTypes]?.add(handler)
    }


    fun submitEvent(event: GameEvent) {

        eventHandlers[event.type]?.let { handlers ->
            handlers.forEach { it.handle(event) }
        }
    }

}