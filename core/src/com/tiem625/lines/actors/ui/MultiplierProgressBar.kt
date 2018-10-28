package com.tiem625.lines.actors.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.asTextureDrawable
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

fun pixmapTextureRegion(color: Color,
                        width: Int,
                        height: Int
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

class MultiplierProgressBar(
        x: Float,
        y: Float,
        width: Float,
        height: Float
) : ProgressBar(0f, GridGlobals.MAX_BAR_PROGRESS, 1f, true,
        ProgressBar.ProgressBarStyle().apply {
            this.background = pixmapTextureRegion(
                    color = Color.BLACK,
                    width = width.toInt(),
                    height = height.toInt()
            )
            this.knob = pixmapTextureRegion(
                    color = Color.BLUE,
                    width = width.toInt(),
                    height = 0
            )
            this.knobBefore = pixmapTextureRegion(
                    Color.BLUE,
                    width = width.toInt(),
                    height = height.toInt()
            )
        }

) {

    init {

        this.x = x
        this.y = y
        this.width = width
        this.height = height

        value = 0f
        setAnimateDuration(0.7f)
        setBounds(x, y, width, height)
        debug()

    }

    public fun addProgress(amount: Float) {

        val newTotal = (value + amount) % GridGlobals.MAX_BAR_PROGRESS

        if (newTotal < value + amount) {
            EventSystem.submitEvent(GameEventTypes.SCORE_PROGRESS_FULL)
        }

        value = newTotal
    }
}