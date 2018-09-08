package com.tiem625.lines.stages

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals

class MainMenu(viewport: Viewport, appearDelay: Float) : Stage(viewport) {

    val MENU_HEIGHT = 150.0f
    val TEXT_FLICKER_DELAY = 1.0f

    val textsGroup = Group()


    val startGameText = Label("Press any button to start game!".toUpperCase(), Label.LabelStyle(
            GridGlobals.pointsLabelFont, Color.YELLOW
    )).apply {
        isVisible = false
        addAction(Actions.repeat(-1, Actions.sequence(
                Actions.hide(),
                Actions.delay(TEXT_FLICKER_DELAY),
                Actions.show(),
                Actions.delay(TEXT_FLICKER_DELAY)
        )))
        setFontScale(3f)
    }

    init {

        textsGroup.addAction(Actions.delay(appearDelay,
                Actions.run {
                    this.addActor(startGameText)
                    startGameText.addAction(Actions.moveBy(0.0f, MENU_HEIGHT))
                }
        ))
        this.addActor(textsGroup)
    }

}