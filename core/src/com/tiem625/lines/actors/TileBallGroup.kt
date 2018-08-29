package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.tiem625.lines.GameRuntime
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

    override fun toString(): String {
        return "[Pos: ${gridPos} | select: ${isSelected} | ball: ${ball?.color ?: "N\\A"}]"
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
    }


    private fun updateSelected(selected: Boolean) {
        //this is movement
        if (this.isSelected != selected) {

            println("AT GROUND ZERO: ${grid.grid[0][0]}")

            updateTileColor(selected)
            //started selection of this tile
            if (selected) {
                GameRuntime.selectedTileGroup?.let {
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
                                        GridGlobals.removeBall(
                                                it,
                                                grid
                                        )
                                    },
                                    *nodePath.map { node ->
                                        Actions.moveTo(
                                                node.tile.width * node.gridPos.second.toFloat(),
                                                node.tile.height * node.gridPos.first.toFloat(),
                                                Gdx.graphics.deltaTime * 10,
                                                Interpolation.linear
                                        )
                                    }.toTypedArray(),
                                    Actions.run {
                                        //transfer ball
                                        GridGlobals.attachBall(
                                                ball,
                                                tileTo = this
                                        )
                                        grid.checkGridUpdates(this)
                                        it.updateSelected(false)
                                    }
                            ))

                        } else {

                            println("Path from ${it.gridPos} to ${this.gridPos} not found... :(")
                        }
                    }
                    it.updateSelected(false)
                }
                GameRuntime.selectedTileGroup = this
            } else {
                if (this == GameRuntime.selectedTileGroup) {
                    GameRuntime.selectedTileGroup = null
                }
            }

            this.isSelected = selected
        }
    }

}