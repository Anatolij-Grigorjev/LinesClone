package com.tiem625.lines.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group

class TileBallGroup(val tile: Tile) : Group() {

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
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }

}