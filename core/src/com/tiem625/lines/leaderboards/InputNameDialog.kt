package com.tiem625.lines.leaderboards

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.asDrawable
import com.tiem625.lines.assets.Assets

class InputNameDialog(stage: Stage): Dialog("Input your name", WindowStyle().apply {
    titleFont = GridGlobals.pointsLabelFont
    titleFontColor = Color.BLACK
    background = Assets.tileTexture.asDrawable()
}) {

    init {
        this.stage = stage



        contentTable.add(TextField(
                "Your name here...",
                TextField.TextFieldStyle().apply {
                    font = GridGlobals.pointsLabelFont
                    fontColor = Color.BLACK

                }
        ))


    }

    fun show(): Dialog = this.show(stage)
}