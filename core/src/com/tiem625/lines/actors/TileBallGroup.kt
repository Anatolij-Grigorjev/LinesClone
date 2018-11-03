package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.tiem625.lines.*
import com.tiem625.lines.assets.AudioPlayer
import com.tiem625.lines.constants.SoundFx
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

class TileBallGroup(val gridPos: Pair<Int, Int>, val tile: Tile) : Group() {

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

    val SINGLE_TILE_MOVE_TIME = 0.16f;


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
                if (!GameRuntime.ballMoving)
                    updateIsSelected(!isSelected)

                return true
            }
        })
    }


    private fun updateIsSelected(selected: Boolean) {
        //this is movement
        if (this.isSelected != selected) {

            updateTileColor(selected)
            //started selection of this tile
            if (selected) {
                AudioPlayer.playSfx(SoundFx.SELECT_TILE)
                GameRuntime.selectedTileGroup?.let {
                    //empty to balled - transfer ball
                    //empty to empty - change selection
                    //ball to ball - change selection

                    //if same ball state
                    if (!GridGlobals.sameBallState(this, it)) {

                        val nodePath = DefaultGraphPath<TileBallGroup>()

                        if (GameRuntime.pathFinder.searchNodePath(
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
                                        GameRuntime.ballMoving = true
                                        EventSystem.submitEvent(GameEventTypes.GROUP_REMOVE_BALL, it)
                                    },
                                    *nodePath.map { node ->
                                        Actions.moveTo(
                                                node.tile.width * node.gridPos.second.toFloat(),
                                                node.tile.height * node.gridPos.first.toFloat(),
                                                SINGLE_TILE_MOVE_TIME,
                                                Interpolation.linear
                                        )
                                    }.toTypedArray(),
                                    Actions.run {
                                        //transfer ball
                                        GridGlobals.attachBall(
                                                ball,
                                                tileTo = this
                                        )
                                        GameRuntime.decreaseFrozenMoves()
                                        EventSystem.submitEvent(GameEventTypes.UPDATE_GRID, this)
                                        it.updateIsSelected(false)
                                        GameRuntime.ballMoving = false
                                        //this was not a combo-creating addition
                                        if (!GameRuntime.justPoppedBalls && GameRuntime.currentPointsMultiplier > 1f) {
                                            //reduce multiplier by step
                                            GameRuntime.currentPointsMultiplier = clamp(
                                                    GameRuntime.currentPointsMultiplier - GridGlobals.STREAK_MULTIPLIER_ADJUST,
                                                    1f,
                                                    Float.MAX_VALUE
                                            )
                                        }
                                    }
                            ))

                        } else {

                            println("Path from ${it.gridPos} to ${this.gridPos} not found... :(")
                        }
                    }
                    it.updateIsSelected(false)
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