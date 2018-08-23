package com.tiem625.lines.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.tiem625.lines.assets.Assets

class GridHUDBackground(x: Float, y: Float, width: Float, height: Float) : Actor() {

    val texture: Texture

    init {
        Assets.manager.finishLoadingAsset(Assets.tile)
        texture = Assets.manager.get(Assets.tile)

        this@GridHUDBackground.x = x
        this@GridHUDBackground.y = y
        this@GridHUDBackground.width = width
        this@GridHUDBackground.height = height
        color = Color.YELLOW
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.let {
            it.setColor(color.r, color.g, color.b, color.a * parentAlpha)
            it.draw(texture, x, y, width, height)
        }
    }
}