package com.tiem625.lines.leaderboards

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals

class InputNameDialog(stage: Stage) : Dialog("High Score!!!", GridGlobals.gameSkin) {

    val field = TextField(
            "Enter your name...", GridGlobals.gameSkin)

    init {

        this.stage = stage

        contentTable.add(Label("Enter your name for the leaderboards:", GridGlobals.gameSkin))
        contentTable.row()
        contentTable.add(field)

        button("Cancel", null)
        button("OK", field.text)
    }

    override fun result(`object`: Any?) {
        if (`object` == null) {
            println("Player decided not to do leaderboard... :(")
        } else {
            //returned objet is name from input
            val newName = `object` as String
            println("Adding leaderboard record for $newName and ${GameRuntime.currentPoints} points...")
        }
    }

    fun show(): Dialog {
        field.selectAll()
        return this.show(stage)
    }
}