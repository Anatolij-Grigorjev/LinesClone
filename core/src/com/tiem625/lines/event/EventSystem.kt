package com.tiem625.lines.event

object EventSystem {

    val eventHandlers = mapOf(
            *GameEventTypes.values().map { it to mutableListOf<GameEventHandler>() }.toTypedArray()
    )

    fun addHandler(eventTypes: GameEventTypes, handler: GameEventHandler) {

        eventHandlers[eventTypes]?.add(handler)
    }
    fun addHandler(eventTypes: GameEventTypes, handler: (GameEvent) -> Any) {

        eventHandlers[eventTypes]?.add(object : GameEventHandler() {
            override fun handle(event: GameEvent) {
                handler(event)
            }
        })
    }


    fun submitEvent(event: GameEvent) {

        eventHandlers[event.type]?.let { handlers ->
            handlers.forEach { it.handle(event) }
        }
    }

}