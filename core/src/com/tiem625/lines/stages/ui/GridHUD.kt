package com.tiem625.lines.stages.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.ui.GridHUDBackground

class GridHUD(viewport: Viewport) : Stage(viewport) {


    val background = GridHUDBackground(
            x = 0.0f,
            y = viewport.worldHeight - GridGlobals.HUD_HEIGHT,
            width = viewport.worldWidth,
            height = GridGlobals.HUD_HEIGHT
    ).apply {
        this@GridHUD.addActor(this)
    }

}