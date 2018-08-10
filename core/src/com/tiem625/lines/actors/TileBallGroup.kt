package com.tiem625.lines.actors

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.tiem625.lines.GridConfig
import com.tiem625.lines.stages.TilesGrid

class TileBallGroup(val grid: TilesGrid, val tile: Tile) : Group() {

    companion object {

        fun sameBallState(g1: TileBallGroup, g2: TileBallGroup): Boolean =
                (g1.ball != null && g2.ball != null) ||
                        (g1.ball == null && g2.ball == null)
    }

    private fun updateTileColor(selected: Boolean) {
        if (selected) {
            tile.color = GridConfig.TILE_SELECTED_COLOR
        } else {
            tile.color = GridConfig.TILE_NORMAL_COLOR
        }
    }

    var isSelected = false

    var ball: Ball? = null
        // perform actor manipulations when ball changes
        set(value) {

            //remove previous ball
            field?.let {
                removeActor(it)
            }

            //add new ball
            value?.let {
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
            var ballTransfered = false
            updateTileColor(selected)
            //started selection of this tile
            if (selected) {
                GridConfig.selectedTileGroup?.let {
                    //empty to balled - transfer ball
                    //empty to empty - change selection
                    //ball to ball - change selection

                    //if same ball state
                    if (!TileBallGroup.sameBallState(this, it)) {
                        //transfer ball
                        if (this.ball == null) {
                            this.ball = it.ball
                            it.ball = null
                        } else {
                            it.ball = this.ball
                            this.ball = null
                        }
                        ballTransfered = true
                        grid.checkGridUpdates(if (this.ball != null) this else it)
                    }
                    //change selection
                    it.updateSelected(false)
                }
                GridConfig.selectedTileGroup = this
            } else {
                if (this == GridConfig.selectedTileGroup) {
                    GridConfig.selectedTileGroup = null
                }
            }

            this.isSelected = selected
            //clear selection
            if (ballTransfered) {
                updateSelected(false)
            }
        }
    }

}