package com.tiem625.lines.event

import java.util.*

object EventSystem {

    data class GameEventTypeHandler(
            val type: GameEventTypes,
            val handler: (GameEvent) -> Unit
    )

    val eventHandlers = mutableMapOf(
            *GameEventTypes.values().map {
                it to mutableListOf<GameEventTypeHandler>()
            }.toTypedArray()
    )

    val handlersByKey = mutableMapOf<String, GameEventTypeHandler>()

    /**
     * Add handler for specified event type,
     * return unique handler UUID key by which its possible to remove it later
     */
    fun addHandler(eventType: GameEventTypes, handlerMethod: (GameEvent) -> Unit): String {
        return UUID.randomUUID().toString().apply {
            val typeHandler = GameEventTypeHandler(type = eventType, handler = handlerMethod)
            (eventHandlers[eventType] ?: mutableListOf<GameEventTypeHandler>().apply {
                eventHandlers[eventType] = this
            }).add(typeHandler)
            handlersByKey[this] = typeHandler
        }
    }

    fun removeHandler(handlerKey: String) {

        handlersByKey[handlerKey]?.let { typeHandler ->
            eventHandlers[typeHandler.type]?.remove(typeHandler)
        }
    }


    fun submitEvent(eventType: GameEventTypes, data: Any? = null) {
        submitEvent(GameEvent(eventType, data))
    }

    private fun submitEvent(event: GameEvent) {

        val handlersList = eventHandlers[event.type]

        println("submitting event ${event.type} to ${handlersList?.size ?: 0} handlers...")

        handlersList?.let { handlers ->
            handlers.forEach { handler ->
                handler.handler(event)
            }
        }
    }

}