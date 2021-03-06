package com.tiem625.lines.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.tiem625.lines.assets.Assets

class Ball(width: Float,
           height: Float,
           color: Color,
           gridPoxX: Int,
           gridPosY: Int) : Actor() {

    val texture: Texture

    var gridPos = Pair(gridPoxX, gridPosY)

    init {
        this.color = color
        this.width = width
        this.height = height

        texture = Assets.ballTexture

        setBounds(x, y, width, height)
    }

    fun resetPosition() {
        x = 0.0f
        y = 0.0f
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