package com.tiem625.lines.leaderboards

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

class InputNameDialog(stage: Stage) : Dialog("High Score!!!", GridGlobals.gameSkin) {

    val fieldPadding = 10f
    val buttonWidth = 150f

    val textField = TextField("", GridGlobals.gameSkin)

    init {

        this.stage = stage
        isModal = true
        contentTable.add(
                Label("Enter your name for the leaderboards:", GridGlobals.gameSkin)
        ).padTop(fieldPadding)
        contentTable.row()
        contentTable.add(textField).left().padBottom(fieldPadding * 2).padTop(fieldPadding / 2)

        setObject(TextButton("Cancel", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth).padBottom(fieldPadding).padRight(fieldPadding)
        }, null)
        setObject(TextButton("OK", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth).padBottom(fieldPadding)
        }, textField)
    }

    override fun result(`object`: Any?) {
        EventSystem.submitEvent(GameEventTypes.DIALOG_DISMISS)
        if (`object` == null) {
            println("Player decided not to do leaderboard... :(")
        } else {
            //returned object is name from input
            val nameField = `object` as TextField
            println("Adding leaderboard record for ${nameField.text} and ${GameRuntime.currentPoints} points...")
        }
    }

    fun show(): Dialog {
        return this.show(stage).apply {
            stage.keyboardFocus = textField
            textField.width = contentTable.width
            EventSystem.submitEvent(GameEventTypes.DIALOG_APPEAR)
        }
    }
}