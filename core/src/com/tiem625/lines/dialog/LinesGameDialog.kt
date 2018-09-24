package com.tiem625.lines.dialog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

abstract class LinesGameDialog(stage: Stage,
                               title: String,
                               val dialogType: GameDialogTypes) : Dialog(title, GridGlobals.gameSkin) {

    companion object {

        var dialogIsShowing = false
    }


    abstract fun constructDialog()

    abstract fun resolveResultObject(result: Any?)

    abstract fun onShow()

    init {

        this.stage = stage
        isModal = true
        constructDialog()
    }

    override fun result(`object`: Any?) {

        resolveResultObject(`object`)
        dialogIsShowing = false
        EventSystem.submitEvent(GameEventTypes.DIALOG_DISMISS, dialogType)
    }

    fun show(): Dialog {
        return this.show(stage).apply {
            dialogIsShowing = true
            //perform custom initialization
            onShow()
            EventSystem.submitEvent(GameEventTypes.DIALOG_APPEAR, dialogType)
        }
    }

}