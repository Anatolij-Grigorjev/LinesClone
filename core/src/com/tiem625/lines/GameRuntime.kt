package com.tiem625.lines

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import com.tiem625.lines.leaderboards.LeaderboardRecord
import com.tiem625.lines.leaderboards.LeaderboardStage

object GameRuntime {

    var musicOn = true
    var sfxOn = true
    val usedBallColors = mutableListOf(*GridGlobals.BALL_COLORS.toTypedArray())

    var selectedTileGroup: TileBallGroup? = null

    data class PointsChange(
            val delta: Int,
            val newPoints: Int
    )

    var currentPoints: Int = 0
        set(value) {
            val prev = field
            field = value
            EventSystem.submitEvent(GameEventTypes.RECEIVE_POINTS, PointsChange(value - prev, value))
        }

    var frozenMultiplier: Boolean = false
    var currentFrozenMultiplierMoves = 0

    /**
     * Perform setup for multiplier to remain frozen for chosen number of moves
     */
    fun freezeMultiplier() {
        currentPointsMultiplier = GridGlobals.FREEZE_MULTIPLIER_VALUE
        frozenMultiplier = true
        currentFrozenMultiplierMoves = GridGlobals.MAX_FROZEN_MULTIPLIER_MOVES
    }

    fun unfreezeMultiplier() {
        frozenMultiplier = false
        currentPointsMultiplier = 1f
        currentFrozenMultiplierMoves = 0
        EventSystem.submitEvent(GameEventTypes.MULTIPLIER_BONUS_OVER)

    }

    var currentPointsMultiplier: Float = 1.0f
        set(value) {
            //ignore new values if multiplier frozen
            if (!frozenMultiplier) {
                field = value
            } else {
                //NOP
            }
            EventSystem.submitEvent(GameEventTypes.CHANGE_MULTIPLIER, field)
        }
    var recordsHash = ""
    val records = LeaderboardStage.loadStoredRecords() ?: arrayOf(*(0 until GridGlobals.LEADERBOARD_POSITIONS).map {
        LeaderboardRecord.empty()
    }.toTypedArray())

    val currentLowestHigh: Int
        get() = records?.get(GridGlobals.LEADERBOARD_POSITIONS - 1)?.score ?: 0

    /**
     * Every adjustment of multiplier by ADJUST means additional 0.2 points to font scale
     */
    fun multiplierScale(baseScale: Float): Float =
            baseScale + ((GameRuntime.currentPointsMultiplier - 1.0f) / GridGlobals.STREAK_MULTIPLIER_ADJUST) * 0.2f

    fun decreaseFrozenMoves() {
        if (frozenMultiplier) {
            currentFrozenMultiplierMoves = clamp(currentFrozenMultiplierMoves - 1, 0, Int.MAX_VALUE)
            if (currentFrozenMultiplierMoves <= 0) {
                unfreezeMultiplier()
            }
        }
    }


    var justPoppedBalls = false
    var ballMoving = false
    lateinit var pathFinder: IndexedAStarPathFinder<TileBallGroup>
}