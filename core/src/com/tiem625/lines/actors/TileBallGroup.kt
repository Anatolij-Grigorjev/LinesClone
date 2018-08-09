package com.tiem625.lines.actors

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.tiem625.lines.GridConfig
import com.tiem625.lines.stages.TilesGrid

class TileBallGroup(val grid: TilesGrid, val tile: Tile) : Group() {

    var selected = false
        set(value) {
            if (field != value) {
                tile.color = if (value) GridConfig.TILE_SELECTED_COLOR else GridConfig.TILE_NORMAL_COLOR
                //started selection of this tile
                if (value) {
                    GridConfig.selectedTile?.let {



                        //TODO: go through selection logic
                        //
                        //empty to balled - transfer ball
                        //empty to empty - change selection
                        //ball to ball - change selection

                    }
                    GridConfig.selectedTile = tile
                }

                field = value
            }
        }

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
            }

            field = value
        }

    init {
        addActor(tile)
        addListener(object: InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {

                selected != selected



                return true
            }
        })
    }

}