package com.tiem625.lines

import com.badlogic.gdx.graphics.Color
import com.tiem625.lines.actors.Tile

object GridConfig {

    const val WORLD_WIDTH = 640.0f
    const val WORLD_HEIGHT = 640.0f

    const val GRID_ROWS = 8
    const val GRID_COLS = 8

    val TILE_NORMAL_COLOR = Color.WHITE
    val TILE_SELECTED_COLOR = Color.BLUE

    var selectedTile: Tile? = null

    const val TURN_NUM_BALLS = 5 //how many balls get added in a turn
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
}