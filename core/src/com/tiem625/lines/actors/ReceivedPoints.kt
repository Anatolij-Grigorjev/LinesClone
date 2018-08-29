package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.ui.GridHUDBackground

class ReceivedPoints(val pos: Pair<Float, Float>,
                     val area: Pair<Float, Float>,
                     val points: Int,
                     ballColor: Color) : Group() {

    val label: Label
    val targetWidth = area.first
    val targetHeight = area.second
    val floatDistance = 5.0f
    val floatFrames = 55

    private val BASE_FONT_SCALE = 1.5f

    init {
        x = pos.first
        y = pos.second
        width = targetWidth
        height = targetHeight

        println("Points at point ${pos}")
        //fires event internally
        GameRuntime.currentPoints += (points * GameRuntime.currentPointsMultiplier).toInt()

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
            setFontScale(GameRuntime.multiplierScale(BASE_FONT_SCALE))
            //add half of target width to start at center,
            //add half of that to see half of label by center
            x = targetWidth / 4
            //vertical is centered due to align, so just bump down enough to float
            y = - floatDistance / 2
        }
        val actual = pointsDimensions()
        addActor(GridHUDBackground(0.0f, 0.0f, actual.first, actual.second,
                Color.DARK_GRAY.apply {
                    this.a = 0.5f
                }))
        addActor(label)
    }


    private fun pointsDimensions(): Pair<Float, Float> =
            GridGlobals.glyphLayout.let {
                it.setText(GridGlobals.pointsLabelFont, label.text)
                (it.width to it.height)
            }

}