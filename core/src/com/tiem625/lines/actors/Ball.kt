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

        Assets.manager.finishLoadingAsset(Assets.ball)
        texture = Assets.manager.get(Assets.ball)

        setBounds(x, y, width, height)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {

        batch?.draw(
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