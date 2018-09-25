package com.tiem625.lines.dialog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

class LeaveScoreDialog(stage: Stage) : LinesGameDialog(
        stage,
        "Are you sure?",
        GameDialogTypes.HIGHSCORE_LEAVE_DIALOG
) {

    var buttonWidth: Float = 0f

    private enum class DialogChoices {
        QUIT, CANCEL, RECORD_SCORE
    }

    override fun constructDialog() {

        contentTable.add(Label("Quit now?\n" +
                "Your score (${GameRuntime.currentPoints}) qualifies for the leaderboards!\n" +
                "Maybe record it first?", GridGlobals.gameSkin))
        contentTable.row()

        buttonWidth = 150.0f
        setObject(TextButton("QUIT", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth).padBottom(buttonWidth / 5)
                    .padRight(buttonWidth / 2)
        }, DialogChoices.QUIT)
        setObject(TextButton("CANCEL", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth).padBottom(buttonWidth / 5)
                    .padRight(buttonWidth / 2)
        }, DialogChoices.CANCEL)
        setObject(TextButton("RECORD SCORE", GridGlobals.gameSkin).apply {
            buttonTable.add(this).width(buttonWidth * 1.5f).padBottom(buttonWidth / 5)
        }, DialogChoices.RECORD_SCORE)

    }

    override fun resolveResultObject(result: Any?) {

        val choice = result as DialogChoices

        when (choice) {

            LeaveScoreDialog.DialogChoices.QUIT -> {
                //pretend to close highscore name dialog - ends play session
                EventSystem.submitEvent(GameEventTypes.DIALOG_DISMISS, GameDialogTypes.HIGHSCORE_NAME_DIALOG)
            }
            LeaveScoreDialog.DialogChoices.CANCEL -> {
                println("all good")
            }
            LeaveScoreDialog.DialogChoices.RECORD_SCORE -> {
                EventSystem.submitEvent(GameEventTypes.GAME_OVER, GameRuntime.currentPoints)
            }
        }
    }

    override fun onShow() {
        this.width = stage.viewport.worldWidth
        buttonTable.width = contentTable.width
    }

}