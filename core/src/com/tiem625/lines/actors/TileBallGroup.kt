package com.tiem625.lines.actors

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.stages.TilesGrid

class TileBallGroup(val grid: TilesGrid, val tile: Tile) : Group() {

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
                println("Tile ${this.tile.gridPos} removing ball ${it.color}")
                removeActor(it)
            }

            //add new ball
            value?.let {
                println("Tile ${this.tile.gridPos} adding ball ${it.color}")
                addActor(it)
                it.zIndex = 999
            }

            field = value
        }

    init {
        addActor(tile)
        addListener(object: InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                updateSelected(!isSelected)



                return true
            }
        })
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
                        //transfer ball
                        GridGlobals.transferBall(
                                tileFrom = this,
                                tileTo = it
                        )
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