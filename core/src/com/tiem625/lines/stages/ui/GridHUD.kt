package com.tiem625.lines.stages.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.ui.GridHUDBackground
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEvent
import com.tiem625.lines.event.GameEventHandler
import com.tiem625.lines.event.GameEventTypes
import java.text.DecimalFormat

class GridHUD(viewport: Viewport) : Stage(viewport) {

    val pointsFormat = DecimalFormat("0000")
    val multiplierFormat = DecimalFormat("x0.0")

    val multiplierFieldWidth = 100.0f

    val backgroundGroup = Group().apply {
        x = 0.0f
        y = viewport.worldHeight - GridGlobals.HUD_HEIGHT
        width = viewport.worldWidth
        height = GridGlobals.HUD_HEIGHT

        this@GridHUD.addActor(this)
    }

    val background = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            width = viewport.worldWidth,
            height = GridGlobals.HUD_HEIGHT,
            color = Color.BROWN
    ).apply {
        backgroundGroup.addActor(this)
    }

    val pointsPadding = 15.0f

    val pointsGroup = Group().apply {
        x = pointsPadding + multiplierFieldWidth + pointsPadding
        y = pointsPadding
        //padded left and right
        width = viewport.worldWidth - 3 * pointsPadding - multiplierFieldWidth
        //padded up and down
        height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding

        backgroundGroup.addActor(this)
    }
    val pointsBg = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            //padded left and right
            width = viewport.worldWidth - 3 * pointsPadding - multiplierFieldWidth,
            //padded up and down
            height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding,
            color = Color.BLACK
    ).apply {
        pointsGroup.addActor(this)
    }
    val pointsLabel = Label("0000", Label.LabelStyle(
            GridGlobals.pointsLabelFont,
            Color.WHITE)).apply {
        setAlignment(Align.right)
        pointsGroup.addActor(this)
        setFontScale(
                parent.width / width / 5, parent.height / height / 3
        )
        //center by Y with X in the right align
        x = parent.width - width - pointsPadding
        y = parent.height / 2 - height / 2
    }

    val multiplierGroup = Group().apply {
        x = pointsPadding
        y = pointsPadding
        //specific width
        width = multiplierFieldWidth
        //padded up and down
        height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding

        backgroundGroup.addActor(this)
    }
    val multiplierBg = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            //specific width
            width = multiplierFieldWidth,
            //padded up and down
            height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding,
            color = Color.BLACK
    ).apply {
        multiplierGroup.addActor(this)
    }
    val multiplierLabel = Label("x0.0", Label.LabelStyle(
            GridGlobals.pointsLabelFont,
            Color.WHITE)).apply {
        setAlignment(Align.center)
        multiplierGroup.addActor(this)
        setFontScale(
                GameRuntime.multiplierScale(parent.height / height / 2)
        )
        //center by Y and X
        x = parent.width / 2 - width / 2
        y = parent.height / 2 - height / 2
    }

    init {

        updatePointsLabel()
        updateMultiplierLabel()

        EventSystem.addHandler(GameEventTypes.RECEIVE_POINTS) { event -> updatePointsLabel() }
        EventSystem.addHandler(GameEventTypes.CHANGE_MULTIPLIER) { event -> updateMultiplierLabel() }
    }

    private fun updatePointsLabel() {

        pointsLabel.setText(pointsFormat.format(GameRuntime.currentPoints))
    }

    private fun updateMultiplierLabel() {
        multiplierLabel.setText(multiplierFormat.format(GameRuntime.currentPointsMultiplier))
    }

}