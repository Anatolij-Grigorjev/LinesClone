package com.tiem625.lines.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions

class ReceivedPoints(val pos: Pair<Float, Float>, val points: Int): Actor() {

    val font = BitmapFont()

    init {
        x = pos.first
        y = pos.second
        addAction(Actions.sequence(
                Actions.moveBy(0.0f, 50.0f, Gdx.graphics.deltaTime * 25),
                Actions.removeActor()
        ))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.let {

            font.draw(it, points.toString(), x, y)
        }
    }
}