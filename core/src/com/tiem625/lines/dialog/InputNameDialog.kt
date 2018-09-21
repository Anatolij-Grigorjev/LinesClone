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

    val fieldPadding = 10f
    val buttonWidth = 150f
    val textFieldHeight = 50f

    val textField = TextField("", GridGlobals.gameSkin)

    override fun constructDialog() {
        contentTable.add(
                Label("Enter your name for the leaderboards:", GridGlobals.gameSkin)
        ).padTop(fieldPadding)
        contentTable.row()
        contentTable.add(textField)
                .left()
                .padBottom(fieldPadding * 2)
                .padTop(fieldPadding / 2)
                .height(textFieldHeight)

        setObject(TextButton("Cancel", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth).padBottom(fieldPadding).padRight(fieldPadding)
        }, null)
        setObject(TextButton("OK", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth).padBottom(fieldPadding)
        }, textField)
    }

    override fun resolveResultObject(result: Any?) {
        if (result == null) {
            println("Player decided not to do leaderboard... :(")
        } else {
            //returned object is name from input
            val nameField = result as TextField
            println("Adding leaderboard record for ${nameField.text} and ${GameRuntime.currentPoints} points...")

            EventSystem.submitEvent(GameEventTypes.LEADERBOARD_ENTRY, (nameField.text to GameRuntime.currentPoints))
        }
    }

    override fun onShow() {
        textField.width = contentTable.width
        stage.keyboardFocus = textField
    }
}