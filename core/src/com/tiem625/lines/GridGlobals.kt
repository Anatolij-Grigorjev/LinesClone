package com.tiem625.lines

import com.badlogic.gdx.graphics.Color
import com.tiem625.lines.actors.TileBallGroup

object GridGlobals {

    const val WORLD_WIDTH = 640.0f
    const val WORLD_HEIGHT = 640.0f

    const val GRID_ROWS = 8
    const val GRID_COLS = 8

    val TILE_NORMAL_COLOR: Color = Color.WHITE
    val TILE_SELECTED_COLOR: Color = Color.BLUE

    var selectedTileGroup: TileBallGroup? = null

    const val TURN_NUM_BALLS = 5 //how many balls get added in a turn
    const val POP_NUM_BALLS = 4 // num balls to align in a single pattern
    const val TILE_BALL_GUTTER = 0.0f

    val BALL_COLORS = listOf<Color>(
            Color.RED,
            Color.BLUE,
            Color.YELLOW
    )

    //all ball positions used during the game. when this list runs out, its over
    //disappearing groups add their positions back into this
    val ballPositions = (0 until GRID_ROWS).map { rIdx ->
        (0 until GRID_COLS).map { cIdx ->
            rIdx to cIdx
        }
    }.flatten().toMutableList().shuffled()

    fun sameBallState(g1: TileBallGroup, g2: TileBallGroup): Boolean =
            (g1.ball != null && g2.ball != null) ||
                    (g1.ball == null && g2.ball == null)

    fun transferBall(tileFrom: TileBallGroup, tileTo: TileBallGroup) {
        //no balls on the tiles or balls on all tiles - cant do nuthin'
        if (sameBallState(tileFrom, tileTo))
            return

        //if ball on other side before call - flip arguments
        if (tileFrom.ball == null && tileTo.ball != null) {
            return transferBall(tileTo, tileFrom)
        }

        //move ball and update positions
        val theBall = tileFrom.ball!!
        tileTo.ball = theBall
        tileFrom.ball = null
        tileTo.ball!!.gridPos = tileTo.tile.gridPos

        ballPositions.remove(tileTo.tile.gridPos)
        ballPositions.add(tileFrom.tile.gridPos)
        ballPositions.shuffled()
    }
}