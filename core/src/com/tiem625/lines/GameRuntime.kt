package com.tiem625.lines

import com.tiem625.lines.actors.TileBallGroup

object GameRuntime {

    var selectedTileGroup: TileBallGroup? = null
    var currentPoints: Int = 0

    var currentPointsMultiplier: Float = 1.0f
    var justPoppedBalls = false
}