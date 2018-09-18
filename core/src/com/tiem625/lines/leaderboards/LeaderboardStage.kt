package com.tiem625.lines.leaderboards

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import java.io.StringWriter
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

    val records = loadStoredRecords() ?: arrayOf(*(0 until GridGlobals.LEADERBOARD_POSITIONS).map {
        LeaderboardRecord.empty()
    }.toTypedArray())

    init {
        val mainTable = root.align(Align.top)
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

        //add evnet handler
        EventSystem.addHandler(GameEventTypes.LEADERBOARD_ENTRY) { event ->

            val entry = event.data as Pair<String, Int>

            val firstSmallerIdx = records.indexOfFirst { it.score < entry.second }

            //if some values actaully are smaller than this
            if (firstSmallerIdx >= 0) {


            }
        }
    }

    fun storeRecords() {

        val leaderBoardsFile = Gdx.files.local(GridGlobals.LEADERBOARD_FILENAME)

        leaderBoardsFile.writeString(
                JsonWriter(StringWriter()).let { writer ->
                    writer.array()
                    records.forEach { record ->
                        writer.`object`()
                        writer.set("name", record.name)
                        writer.set("score", record.score)
                    }
                    writer.flush()
                    writer.writer.toString()
                },
                false,
                Charsets.UTF_8.displayName()
        )
    }

    private fun loadStoredRecords(): Array<LeaderboardRecord>? {

        val leaderBoardsFile = Gdx.files.local(GridGlobals.LEADERBOARD_FILENAME)

        if (!leaderBoardsFile.exists()) return null

        val data = leaderBoardsFile.readString(Charsets.UTF_8.displayName())
        return JsonReader().parse(data).let {
            if (!it.isArray) return null

            it.map { jsonValue ->
                LeaderboardRecord(
                        name = jsonValue["name"]?.asString() ?: return null,
                        score = jsonValue["score"]?.asInt() ?: return null
                )
            }
        }.toTypedArray()
    }
}