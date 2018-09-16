package com.tiem625.lines.stages

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals

class LeaderboardStage(viewport: Viewport): Stage(viewport) {

    val initialBoardOffset = -1000f

    val appearDuration = 1f

    val root = Window("Leaderboard: ", GridGlobals.gameSkin).apply {
        y = initialBoardOffset
    }

    val rowSpacing = 15f

    init {
        val mainTable = root.center()
                .debugTable()
        mainTable.padTop(rowSpacing)

        addActor(root.apply {
            addAction(Actions.moveBy(0f, -initialBoardOffset, appearDuration))
        })
    }
}