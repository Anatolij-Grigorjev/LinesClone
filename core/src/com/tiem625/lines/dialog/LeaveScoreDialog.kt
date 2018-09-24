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

    val buttonWidth = 50f

    private enum class DialogChoices {
        QUIT, CANCEL, RECORD_SCORE
    }

    override fun constructDialog() {

        contentTable.row()
        contentTable.add(Label("Quit now? Your score (${GameRuntime.currentPoints}) qualifies for the leaderboards! " +
                "Maybe record it at least?", GridGlobals.gameSkin))
        contentTable.row()

        setObject(TextButton("QUIT", GridGlobals.gameSkin), DialogChoices.QUIT)
        setObject(TextButton("CANCEL", GridGlobals.gameSkin), DialogChoices.CANCEL)
        setObject(TextButton("RECORD SCORE", GridGlobals.gameSkin), DialogChoices.RECORD_SCORE)
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
                TODO("generate some event to call highscore name input dialog?")
            }
        }
    }

    override fun onShow() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}