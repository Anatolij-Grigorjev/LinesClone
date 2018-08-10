package com.tiem625.lines.stages

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.tiem625.lines.GridConfig
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.actors.Tile
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.pop
import com.tiem625.lines.random
import com.tiem625.lines.shuffled

class TilesGrid(val numRows: Int,
                val numCols: Int) : Stage(ExtendViewport(GridConfig.WORLD_WIDTH, GridConfig.WORLD_HEIGHT)) {

    val tileWidth: Float = viewport.worldWidth / numRows
    val tileHeight: Float = viewport.worldHeight / numCols

    val grid: Array<Array<TileBallGroup>> = (tileWidth to tileHeight).let { (tileWidth, tileHeight) ->

        Array(numRows) { rowIdx ->
            Array(numCols) { colIdx ->
                TileBallGroup(this, Tile(tileWidth, tileHeight).apply {
                    zIndex = 0
                }).apply {
                    x = tileWidth * colIdx
                    y = tileHeight * rowIdx
                    tile.group = this
                    this@TilesGrid.addActor(this)
                }
            }
        }
    }

    fun addNewBalls():Boolean {

        //take first N new ball positions
        val newPositions = GridConfig.ballPositions.pop(GridConfig.TURN_NUM_BALLS)
        //reshuffle smaller list
        GridConfig.ballPositions.shuffled()

        newPositions.forEach { pos ->

            grid[pos.first][pos.second].ball = Ball(
                    width = tileWidth - GridConfig.TILE_BALL_GUTTER,
                    height = tileHeight - GridConfig.TILE_BALL_GUTTER,
                    color = GridConfig.BALL_COLORS.random(),
                    gridPoxX = pos.first,
                    gridPosY = pos.second
            ).apply {
                zIndex = 999
            }
        }

        //return if we had balls
        return newPositions.size == GridConfig.TURN_NUM_BALLS
    }

    fun checkGridUpdates(vararg aroundBalls: TileBallGroup) {
        aroundBalls.forEach { balledGroup ->
            //only do things if group has ball
            balledGroup.ball?.let { ball ->

                //search area around ball
                val markedSurroundGroups = markAroundBall(ball)

                if (markedSurroundGroups.size >= GridConfig.POP_NUM_BALLS) {
                    //create removable group
                    Group().apply {
                        markedSurroundGroups.forEach {
                            this.addActor(it.ball!!)
                            this.addAction(Actions.run {
                                removeActor(it.ball)
                                it.ball = null
                            })
                        }
                        addAction(Actions.removeActor())
                    }
                }
            }
        }
    }

    fun markAroundBall(ball: Ball): List<TileBallGroup> {
        //for now take first N balls from grid
        return grid.flatten().filter { it.ball != null }.take(GridConfig.POP_NUM_BALLS)
    }


}