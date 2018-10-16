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
    var currentPoints: Int = 0
        set(value) {
            field = value
            EventSystem.submitEvent(GameEventTypes.RECEIVE_POINTS, value)
        }

    //lowest current highscore, barrier for name entry
    var currentPointsMultiplier: Float = 1.0f
        set(value) {

            field = value
            EventSystem.submitEvent(GameEventTypes.CHANGE_MULTIPLIER, value)
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


    var justPoppedBalls = false
    var ballMoving = false
    lateinit var pathFinder: IndexedAStarPathFinder<TileBallGroup>
}