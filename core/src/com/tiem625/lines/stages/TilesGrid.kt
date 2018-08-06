package com.tiem625.lines.stages

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tiem625.lines.*
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.actors.Tile

class TilesGrid(val numRows: Int,
                val numCols: Int) : Stage(ExtendViewport(GridConfig.WORLD_WIDTH, GridConfig.WORLD_HEIGHT)) {

    val tileWidth: Float = viewport.worldWidth / numRows
    val tileHeight: Float = viewport.worldHeight / numCols

    val grid: Array<Array<Tile>> = (tileWidth to tileHeight).let { (tileWidth, tileHeight) ->

        Array(numRows) { rowIdx ->
            Array(numCols) { colIdx ->
                Tile(tileWidth, tileHeight).apply {
                    x = tileWidth * colIdx
                    y = tileHeight * rowIdx
                    this@TilesGrid.addActor(this)
                }
            }
        }
    }

    fun addNewBalls() {

        val newPositions = GridConfig.ballPositions.pop(GridConfig.TURN_NUM_BALLS)
        //reshuffle new list
        GridConfig.ballPositions.shuffled()

        newPositions.forEach { pos ->

            grid[pos.first][pos.second].addActor(Ball(
                    width = tileWidth - GridConfig.TILE_BALL_GUTTER,
                    height = tileHeight - GridConfig.TILE_BALL_GUTTER,
                    color = GridConfig.BALL_COLORS.random(),
                    gridPoxX = pos.first,
                    gridPosY = pos.second
            ))
        }

    }


}