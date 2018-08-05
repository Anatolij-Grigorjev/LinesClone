package com.tiem625.lines.stages

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tiem625.lines.LinesGame
import com.tiem625.lines.actors.Tile

class TilesGrid(val numRows: Int,
                val numCols: Int) : Stage(ExtendViewport(LinesGame.WORLD_WIDTH, LinesGame.WORDL_HEIGHT)) {

    val grid: Array<Array<Tile?>> =
            Array(numRows) { rowIdx ->
                Array(numCols) { colIdx ->
                    null as Tile?
                }
            }

    

}