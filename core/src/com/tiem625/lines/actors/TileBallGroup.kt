package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.distanceTo
import com.tiem625.lines.stages.TilesGrid
import com.tiem625.lines.toIndex

class TileBallGroup(val grid: TilesGrid, val gridPos: Pair<Int, Int>, val tile: Tile) : Group() {

    private fun updateTileColor(selected: Boolean) {
        if (selected) {
            tile.color = GridGlobals.TILE_SELECTED_COLOR
        } else {
            tile.color = GridGlobals.TILE_NORMAL_COLOR
        }
    }

    var isSelected = false

    var ball: Ball? = null
    // perform actor manipulations when ball changes
        set(value) {

            //remove previous ball
            field?.let {
                println("Tile ${this.gridPos} removing ball ${it.color}")
                removeActor(it)
            }

            //add new ball
            value?.let {
                println("Tile ${this.gridPos} adding ball ${it.color}")
                addActor(it)
                it.zIndex = 999
            }

            field = value
        }

    init {
        addActor(tile)
        addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                updateSelected(!isSelected)



                return true
            }
        })
        cullingArea = Rectangle(0.0f, 0.0f, grid.numRows * tile.width, grid.numCols * tile.height)
    }


    private fun updateSelected(selected: Boolean) {
        if (this.isSelected != selected) {
            var ballTransferred = false
            updateTileColor(selected)
            //started selection of this tile
            if (selected) {
                GridGlobals.selectedTileGroup?.let {
                    //empty to balled - transfer ball
                    //empty to empty - change selection
                    //ball to ball - change selection

                    //if same ball state
                    if (!GridGlobals.sameBallState(this, it)) {

                        val nodePath = DefaultGraphPath<TileBallGroup>()

                        if (grid.pathFinder.searchNodePath(
                                        it,
                                        this,
                                        { start, end ->
                                            start.gridPos.distanceTo(end.gridPos).toIndex().toFloat()
                                        },
                                        nodePath
                                )
                        ) {

                            val ball = this.ball ?: it.ball!!

                            println("Moving ball ${ball.color} between ${it.gridPos} and ${this.gridPos} via ${nodePath.joinToString { it.gridPos.toString() }}")

                            ball.addAction(Actions.sequence(
                                    Actions.run {
                                        it.removeActor(ball)
                                        grid.addActor(ball)
                                    },
                                    *nodePath.map { node ->
                                        Actions.moveTo(
                                                node.tile.width * node.gridPos.first.toFloat(),
                                                node.tile.height * node.gridPos.second.toFloat(),
                                                Gdx.graphics.deltaTime * 5,
                                                Interpolation.linear
                                        )
                                    }.toTypedArray(),
                                    Actions.run {
                                        //transfer ball
                                        GridGlobals.transferBall(
                                                tileFrom = this,
                                                tileTo = it
                                        )
                                    }
                            ))

                        } else {

                            println("Path from ${it.gridPos} to ${this.gridPos} not found... :(")
                        }

                        ballTransferred = true
                        grid.checkGridUpdates(if (this.ball != null) this else it)
                    }
                    //change selection
                    it.updateSelected(false)
                }
                GridGlobals.selectedTileGroup = this
            } else {
                if (this == GridGlobals.selectedTileGroup) {
                    GridGlobals.selectedTileGroup = null
                }
            }

            this.isSelected = selected
            //clear selection
            if (ballTransferred) {
                updateSelected(false)
            }
        }
    }

}