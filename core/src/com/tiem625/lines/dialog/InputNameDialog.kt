package com.tiem625.lines.dialog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

class InputNameDialog(stage: Stage) : LinesGameDialog(stage, "High Score!!!".toUpperCase()) {

    companion object {
        const val FIELD_PADDING = 10f
        const val BUTTON_WIDTH = 150f
        const val TEXT_FIELD_HEIGHT = 50f
    }

    lateinit var textField: TextField

    override fun constructDialog() {
        textField = TextField("", GridGlobals.gameSkin)
        contentTable.add(
                Label("Enter your name for the leaderboards:", GridGlobals.gameSkin)
        ).padTop(FIELD_PADDING)
        contentTable.row()
        contentTable.add(textField)
                .left()
                .padBottom(FIELD_PADDING * 2)
                .padTop(FIELD_PADDING / 2)
                .height(TEXT_FIELD_HEIGHT)

        setObject(TextButton("Cancel", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(BUTTON_WIDTH).padBottom(FIELD_PADDING).padRight(FIELD_PADDING)
        }, null)
        setObject(TextButton("OK", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(BUTTON_WIDTH).padBottom(FIELD_PADDING)
        }, textField)
    }

    override fun resolveResultObject(result: Any?) {

        (result as? TextField)?.let { nameField ->

            println("Adding leaderboard record for ${nameField.text} and ${GameRuntime.currentPoints} points...")

            EventSystem.submitEvent(GameEventTypes.LEADERBOARD_ENTRY, (nameField.text to GameRuntime.currentPoints))

        } ?: println("Player decided not to do leaderboard... :(")
    }

    override fun onShow() {
        textField.width = contentTable.width
        stage.keyboardFocus = textField
    }
}