package com.tiem625.lines.leaderboards

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import java.io.StringWriter
import java.text.DecimalFormat

class LeaderboardStage(viewport: Viewport) : Stage(viewport) {

    companion object {

        const val INITIAL_BOARD_OFFSET = -GridGlobals.WORLD_HEIGHT
        const val APPEAR_ANIMATION_SEC = 0.8f
        const val ROW_SPACING = 15f
        val POINTS_FORMATTER: DecimalFormat = DecimalFormat("00000000")
        val PLACE_FORMATTER: DecimalFormat = DecimalFormat("'#'00'. '")

        var loadedFile = false
    }


    val root = Window("Leaderboard: ".toUpperCase(), GridGlobals.gameSkin).apply {
        width = viewport.worldWidth
        titleTable.cells[0].expand().fill()
        titleLabel.setAlignment(Align.left)
    }


    val records = loadStoredRecords() ?: arrayOf(*(0 until GridGlobals.LEADERBOARD_POSITIONS).map {
        LeaderboardRecord.empty()
    }.toTypedArray())

    var recordsHash = ""

    init {

        updateLowestHigh()

        val mainTable = root.align(Align.top)
//                .debug()

        records.forEachIndexed { idx, record ->
            mainTable.row().fillX().padTop(ROW_SPACING)

            mainTable.add(PLACE_FORMATTER.format(idx + 1))
                    .left()
                    .width(Value.percentWidth(0.1f, mainTable))
                    .fill()

            mainTable.add(Label(record.name, GridGlobals.gameSkin).apply {
                setAlignment(Align.left)
            }).left().expandX().expand()

            mainTable.add(Label(POINTS_FORMATTER.format(record.score), GridGlobals.gameSkin).apply {
                setAlignment(Align.right)
            }).right().expand().fill()
        }

        addActor(root.apply {
            y = INITIAL_BOARD_OFFSET
            addAction(Actions.sequence(
                    Actions.moveBy(0f, -INITIAL_BOARD_OFFSET, APPEAR_ANIMATION_SEC),
                    Actions.run {
                        root.height = viewport.worldHeight
                    }
            ))
        })

        //add event handler
        EventSystem.addHandler(GameEventTypes.LEADERBOARD_ENTRY) { event ->

            val entry = event.data as Pair<String, Int>

            val firstSmallerIdx = records.indexOfFirst { it.score <= entry.second }

            //if some values actually are smaller than this
            if (firstSmallerIdx >= 0) {

                //shit elements lower by one position down
                (firstSmallerIdx until records.size - 1).forEach { idx ->
                    records[idx + 1] = records[idx]
                }
                records[firstSmallerIdx] = LeaderboardRecord(
                        name = entry.first,
                        score = entry.second
                )
            }

            updateLowestHigh()
        }

        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                when (keycode) {

                    Input.Keys.ESCAPE -> {
                        storeRecords()
                        EventSystem.submitEvent(GameEventTypes.GRID_ESCAPE)
                    }
                    else -> {
                        println("No Leaderboards actions for key $keycode :(")
                    }
                }

                return true
            }

        })
    }

    fun storeRecords() {

        val hash = calcRecordsHash(records)

        if (recordsHash == hash) {
            println("Records didn't change, no saving!")
            return
        } else {
            recordsHash = hash
        }

        val leaderBoardsFile = Gdx.files.local(GridGlobals.LEADERBOARD_FILENAME)

        leaderBoardsFile.writeString(
                JsonWriter(StringWriter()).let { writer ->
                    writer.array()
                    records.forEach { record ->
                        writer.`object`()
                        writer.set("name", record.name)
                        writer.set("score", record.score)
                        writer.pop()
                    }
                    writer.pop()
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
        return try {
            JsonReader().parse(data).let {
                if (!it.isArray) return null

                it.map { jsonValue ->
                    LeaderboardRecord(
                            name = jsonValue["name"]?.asString() ?: return null,
                            score = jsonValue["score"]?.asInt() ?: return null
                    )
                }
            }.toTypedArray().apply {
                //calculate records current hash
                recordsHash = calcRecordsHash(this)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
    }

    private fun updateLowestHigh() {
        GameRuntime.currentLowestHigh = records.lastOrNull()?.score ?: 0
    }

    private fun calcRecordsHash(records: Array<LeaderboardRecord>): String =
            records.joinToString(separator = "|") { it.hashCode().toString() }

    override fun dispose() {
        storeRecords()
        super.dispose()
    }
}