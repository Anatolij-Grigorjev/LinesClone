package com.tiem625.lines

import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEvent
import com.tiem625.lines.event.GameEventTypes

object GameRuntime {

    var selectedTileGroup: TileBallGroup? = null
    var currentPoints: Int = 0
    set(value) {
        field = value
        EventSystem.submitEvent(GameEvent(GameEventTypes.RECEIVE_POINTS, value))
    }
    var currentPointsMultiplier: Float = 1.0f
    set(value) {

        field = value
        EventSystem.submitEvent(GameEvent(GameEventTypes.CHANGE_MULTIPLIER, value))
    }

    /**
     * Every adjustment of multiplier by ADJUST means additional 0.2 points to font scale
     */
    fun multiplierScale(baseScale: Float): Float =
            baseScale + ((GameRuntime.currentPointsMultiplier - 1.0f) / GridGlobals.STREAK_MULTIPLIER_ADJUST) * 0.2f



    var justPoppedBalls = false
}