package com.tiem625.lines.stages

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tiem625.lines.LinesGame
import com.tiem625.lines.actors.Tile

class TilesGrid(val numRows: Int,
                val numCols: Int) : Stage(ExtendViewport(LinesGame.WORLD_WIDTH, LinesGame.WORDL_HEIGHT)) {

    val grid: Array<Array<Tile>> = (
            viewport.worldWidth / numRows
                    to viewport.worldHeight / numCols).let { (tileWidth, tileHeight) ->
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


}