package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Align

class ReceivedPoints(val pos: Pair<Float, Float>, val points: Int): Actor() {

    val font = BitmapFont()

    val targetWidth = 25.0f
    val floatDistance = 5.0f
    val floatFrames = 55

    init {
        println("Points at point ${pos}")
        x = pos.first - targetWidth / 2
        y = pos.second + font.xHeight - floatDistance / 2
        addAction(Actions.sequence(
                Actions.moveBy(0.0f, floatDistance, Gdx.graphics.deltaTime * floatFrames),
                Actions.removeActor()
        ))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.let {

            font.draw(it, points.toString(), x, y, targetWidth, Align.center, true)
        }
    }
}