package com.tiem625.lines.event

object EventSystem {

    val eventHandlers = mapOf(
            *GameEventTypes.values().map {
                it to mutableListOf<(GameEvent) -> Unit>()
            }.toTypedArray()
    )

    fun addHandler(eventTypes: GameEventTypes, handler: (GameEvent) -> Unit) {

        eventHandlers[eventTypes]?.add(handler)
    }


    fun submitEvent(eventType: GameEventTypes, data: Any? = null) {
        submitEvent(GameEvent(eventType, data))
    }

    private fun submitEvent(event: GameEvent) {

        eventHandlers[event.type]?.let { handlers ->
            handlers.forEach { it(event) }
        }
    }

}