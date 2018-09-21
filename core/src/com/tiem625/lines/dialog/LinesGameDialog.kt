package com.tiem625.lines.dialog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes

abstract class LinesGameDialog(stage: Stage,
                               val title: String) : Dialog(title, GridGlobals.gameSkin) {


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
        EventSystem.submitEvent(GameEventTypes.DIALOG_DISMISS)
    }

    fun show(): Dialog {
        return this.show(stage).apply {
            onShow()
            EventSystem.submitEvent(GameEventTypes.DIALOG_APPEAR)
        }
    }

}