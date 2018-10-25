package com.tiem625.lines.leaderboards

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.constants.GameScreens
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import com.tiem625.lines.readJSONFile
import com.tiem625.lines.writeJSONFile
import java.text.DecimalFormat

class LeaderboardStage(viewport: Viewport) : Stage(viewport) {

    companion object {

        const val INITIAL_BOARD_OFFSET = -GridGlobals.WORLD_HEIGHT
        const val APPEAR_ANIMATION_SEC = 0.8f
        const val ROW_SPACING = 15f
        val POINTS_FORMATTER: DecimalFormat = DecimalFormat("00000000")
        val PLACE_FORMATTER: DecimalFormat = DecimalFormat("'#'00'. '")

        fun loadStoredRecords(): Array<LeaderboardRecord>? {

            return readJSONFile(GridGlobals.LEADERBOARD_FILENAME) {
                if (!it.isArray) {

                    it.map { jsonValue ->
                        LeaderboardRecord(
                                name = jsonValue["name"]?.asString() ?: "",
                                score = jsonValue["score"]?.asInt() ?: 0
                        )
                    }
                } else null
            }?.toTypedArray()?.apply {
                //calculate records current hash
                GameRuntime.recordsHash = calcRecordsHash(this)
            }

        }

        private fun calcRecordsHash(records: Array<LeaderboardRecord>): String =
                records.joinToString(separator = "|") { it.hashCode().toString() }
    }


    val root = Window("Leaderboard: ".toUpperCase(), GridGlobals.gameSkin).apply {
        width = viewport.worldWidth
        titleTable.cells[0].expand().fill()
        titleLabel.setAlignment(Align.left)
    }

    init {

        val mainTable = root.align(Align.top)
//                .debug()

        GameRuntime.records.forEachIndexed { idx, record ->
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

        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                when (keycode) {

                    Input.Keys.ESCAPE -> {
                        storeRecords()
                        EventSystem.submitEvent(GameEventTypes.STAGE_ESCAPE, GameScreens.LEADERBOARDS)
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

        val hash = calcRecordsHash(GameRuntime.records)

        if (GameRuntime.recordsHash == hash) {
            println("Records didn't change, no saving!")
            return
        } else {
            GameRuntime.recordsHash = hash
        }

        writeJSONFile(GridGlobals.LEADERBOARD_FILENAME) { writer ->
            writer.array()
            GameRuntime.records.forEach { record ->
                writer.`object`()
                writer.set("name", record.name)
                writer.set("score", record.score)
                writer.pop()
            }
            writer.pop()
            writer.flush()
        }
    }

    override fun dispose() {
        storeRecords()
        super.dispose()
    }
}