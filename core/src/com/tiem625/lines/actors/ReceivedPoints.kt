package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.tiem625.lines.GridGlobals

class ReceivedPoints(val pos: Pair<Float, Float>,
                     val area: Pair<Float, Float>,
                     val points: Int,
                     ballColor: Color): Actor() {

    val label: Label
    val targetWidth = area.first
    val targetHeight = area.second
    val floatDistance = 5.0f
    val floatFrames = 55

    init {
        println("Points at point ${pos}")
        //add half of target width to start at center,
        //add half of that to see half of label by center
        x = pos.first  + targetWidth / 4
        //vertical is centered due to align, so just bump down enough to float
        y = pos.second  - floatDistance / 2
        addAction(Actions.sequence(
                Actions.moveBy(0.0f, floatDistance, Gdx.graphics.deltaTime * floatFrames),
                Actions.removeActor()
        ))

        label = Label(points.toString(), Label.LabelStyle(
                GridGlobals.pointsLabelFont,
                //white for blue balls due to background
                if (ballColor != Color.BLUE) ballColor else Color.WHITE
        )).apply {
            setFontScale(1.5f)
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