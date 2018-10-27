package com.tiem625.lines.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.tiem625.lines.asTextureDrawable

class MultiplierProgressBar(
        x: Float,
        y: Float,
        width: Float,
        height: Float
) : Actor() {

    private val progressBar: ProgressBar

    init {

        this.x = x
        this.y = y
        this.width = width
        this.height = height

        progressBar = ProgressBar(0f, 2000f, 1f, true,
                ProgressBar.ProgressBarStyle().apply {
                    this.background = pixmapTextureRegion(Color.BLACK)
                    this.knob = pixmapTextureRegion(
                            color = Color.BLUE,
                            height = 0
                    )
                    this.knobBefore = pixmapTextureRegion(Color.BLUE)
                }).apply {

            value = maxValue / 2
            setAnimateDuration(0.7f)
        }
    }


    private fun pixmapTextureRegion(color: Color,
                                    width: Int = this.width.toInt(),
                                    height: Int = this.height.toInt()
    ): Drawable {

        val pixmap = Pixmap(
                width,
                height,
                Pixmap.Format.RGB888).apply {
            setColor(color)
            fill()
        }

        return pixmap.asTextureDrawable()
    }

}