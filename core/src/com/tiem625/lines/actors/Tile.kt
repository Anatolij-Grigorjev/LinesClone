package com.tiem625.lines.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.tiem625.lines.assets.Assets

class Tile(width: Float, height: Float): Actor() {

    private val texture: Texture

    lateinit var group: TileBallGroup

    init {
        setWidth(width)
        setHeight(height)

        texture = Assets.tileTexture
        setBounds(x, y, width, height)
    }


    override fun draw(batch: Batch?, parentAlpha: Float) {
        val color = this.color
        batch?.let {
            it.setColor(color.r, color.g, color.b, color.a * parentAlpha)
            it.draw(
                    texture,
                    this.x,
                    this.y,
                    this.originX,
                    this.originY,
                    this.width,
                    this.height,
                    this.scaleX,
                    this.scaleY,
                    this.rotation,
                    0,
                    0,
                    texture.width,
                    texture.height,
                    false,
                    false
            )
        }
    }


}