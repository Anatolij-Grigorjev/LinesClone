package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEvent
import com.tiem625.lines.event.GameEventTypes

class ReceivedPoints(val pos: Pair<Float, Float>,
                     val area: Pair<Float, Float>,
                     val points: Int,
                     ballColor: Color) : Actor() {

    val label: Label
    val targetWidth = area.first
    val targetHeight = area.second
    val floatDistance = 5.0f
    val floatFrames = 55

    private val BASE_FONT_SCALE = 1.5f

    init {
        println("Points at point ${pos}")

        EventSystem.submitEvent(
                GameEvent(
                        GameEventTypes.RECEIVE_POINTS,
                        (points * GameRuntime.currentPointsMultiplier).toInt()
                )
        )

        //add half of target width to start at center,
        //add half of that to see half of label by center
        x = pos.first + targetWidth / 4
        //vertical is centered due to align, so just bump down enough to float
        y = pos.second - floatDistance / 2
        addAction(Actions.sequence(
                Actions.moveBy(0.0f, floatDistance, Gdx.graphics.deltaTime * floatFrames),
                Actions.removeActor()
        ))

        val text = "+$points${if (GameRuntime.currentPointsMultiplier > 1.0f) " X ${GameRuntime.currentPointsMultiplier}" else ""}"
        println("doing points text: $text")
        //label text hides multiplier if its not higher than normal
        label = Label(text, Label.LabelStyle(
                GridGlobals.pointsLabelFont,
                //white for blue balls due to background
                if (ballColor != Color.BLUE) ballColor else Color.WHITE
        )).apply {
            //every ADJUST increase
            //should increase font scale by 0.2 from base
            setFontScale(BASE_FONT_SCALE + (
                    ((GameRuntime.currentPointsMultiplier - 1.0f) / GridGlobals.STREAK_MULTIPLIER_ADJUST) * 0.2f)
            )
            width = targetWidth
            height = targetHeight
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.let {
            label.setPosition(x, y, Align.center)
            label.draw(it, parentAlpha)
        }
    }
}