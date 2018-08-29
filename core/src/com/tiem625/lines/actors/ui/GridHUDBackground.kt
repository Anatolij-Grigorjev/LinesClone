package com.tiem625.lines.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class GridHUDBackground(x: Float, y: Float, width: Float, height: Float, color: Color) : Actor() {

    val texture: Texture

    init {

        val pixmap = Pixmap(width.toInt(), height.toInt(), Pixmap.Format.RGB888).apply {
            setColor(color)
            fillRectangle(0, 0, this.width, this.height)
        }
        texture = Texture(pixmap).apply {
            pixmap.dispose()
        }

        this@GridHUDBackground.x = x
        this@GridHUDBackground.y = y
        this@GridHUDBackground.width = width
        this@GridHUDBackground.height = height
        this@GridHUDBackground.color = color
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.let {
            it.draw(texture, x, y, width, height)
        }
    }


}