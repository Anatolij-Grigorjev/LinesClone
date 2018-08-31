package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.ui.GridHUDBackground
import com.tiem625.lines.clamp

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
    private val LABEL_PADDING = 15.0f

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
        val labelScale = GameRuntime.multiplierScale(BASE_FONT_SCALE)
        label = Label(text, Label.LabelStyle(
                GridGlobals.pointsLabelFont,
                //white for blue balls due to background
                if (ballColor != Color.BLUE) ballColor else Color.WHITE
        )).apply {
            setFontScale(labelScale)
            //add half of target width to start at center,
            //add half of that to see half of label by center
            x = targetWidth / 4
            //vertical is centered due to align, so just bump down enough to float
            y = targetHeight / 2 - floatDistance / 2
        }
        val labelSize = GridGlobals.pointsDimensions(label)

        val labelBLockWidth = labelSize.first * labelScale + (LABEL_PADDING * 2)
        val labelBlockHeight = labelSize.second * labelScale + (LABEL_PADDING * 2)
        //clamp this actor position to allow label display
        x = clamp(x, 0.0f, GridGlobals.WORLD_WIDTH - labelBLockWidth)
        y = clamp(y, 0.0f, GridGlobals.WORLD_HEIGHT - labelBlockHeight)

        addActor(GridHUDBackground(
                label.x - LABEL_PADDING,
                label.y - LABEL_PADDING,
                labelBLockWidth,
                labelBlockHeight,
                Color.DARK_GRAY.apply {
                    this.a = 0.5f
                }).apply {

        })
        addActor(label)
    }

}