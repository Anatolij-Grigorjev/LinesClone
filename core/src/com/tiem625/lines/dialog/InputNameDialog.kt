package com.tiem625.lines.dialog

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.click
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

class InputNameDialog(stage: Stage) : LinesGameDialog(stage, "High Score!!!".toUpperCase()) {

    companion object {
        const val FIELD_PADDING = 10f
        const val BUTTON_WIDTH = 150f
        const val TEXT_FIELD_HEIGHT = 50f
    }

    private lateinit var textField: TextField

    lateinit var cancelButton: Button
    lateinit var okButton: Button
    private lateinit var buttonsKeyboardClicker: InputListener

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

        cancelButton = TextButton("Cancel", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(BUTTON_WIDTH).padBottom(FIELD_PADDING).padRight(FIELD_PADDING)
            setProgrammaticChangeEvents(true)
        }
        okButton = TextButton("OK", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(BUTTON_WIDTH).padBottom(FIELD_PADDING)
            setProgrammaticChangeEvents(true)
        }
        buttonsKeyboardClicker = object: InputListener() {
            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                when(keycode) {

                    Input.Keys.ENTER -> {

                        okButton.click()
                        return true
                    }
                    Input.Keys.ESCAPE -> {
                        cancelButton.click()
                        return true
                    }

                }

                return true
            }
        }

        setObject(cancelButton, null)
        setObject(okButton, textField)

        stage.addListener(buttonsKeyboardClicker)
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