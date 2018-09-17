package com.tiem625.lines.leaderboards

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals
import java.text.DecimalFormat

class LeaderboardStage(viewport: Viewport): Stage(viewport) {

    val initialBoardOffset = -GridGlobals.WORLD_HEIGHT

    val appearDuration = 1f

    val root = Window("Leaderboard: ".toUpperCase(), GridGlobals.gameSkin).apply {
        y = initialBoardOffset
        width = viewport.worldWidth
        height = viewport.worldHeight
        titleTable.debug().cells[0].expand().fill()
        titleLabel.setAlignment(Align.left)
    }

    val rowSpacing = 15f
    val pointsFormatter: DecimalFormat = DecimalFormat("00000000")
    val placeFormatter: DecimalFormat = DecimalFormat("'#'00'. '")

    val records = arrayOf(*(0 until GridGlobals.LEADERBOARD_POSITIONS).map {
        LeaderboardRecord.empty()
    }.toTypedArray())

    init {
        val mainTable = root
//                .debug()

        records.forEachIndexed { idx, record ->
            mainTable.row().fillX().padTop(rowSpacing)

            mainTable.add(placeFormatter.format(idx + 1))
                    .left()
                    .width(Value.percentWidth(0.1f, mainTable))
                    .fill()

            mainTable.add(Label(record.name, GridGlobals.gameSkin).apply {
                setAlignment(Align.left)
            }).left().expandX().expand()

            mainTable.add(Label(pointsFormatter.format(record.score), GridGlobals.gameSkin).apply {
                setAlignment(Align.right)
            }).right().expand().fill()
        }

        addActor(root.apply {
            addAction(Actions.moveBy(0f, -initialBoardOffset, appearDuration))
        })
    }
}