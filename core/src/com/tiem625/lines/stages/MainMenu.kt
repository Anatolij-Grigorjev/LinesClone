package com.tiem625.lines.stages

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.constants.MenuItems
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEvent
import com.tiem625.lines.event.GameEventTypes

class MainMenu(viewport: Viewport, appearDelay: Float) : Stage(viewport) {

    val textsGroup = VerticalGroup()

    val SPACING = 20.0f

    val menuOptions = MenuItems.values().map { option ->
        option to Label(option.menuLine.toUpperCase(), Label.LabelStyle(
                GridGlobals.pointsLabelFont, Color.YELLOW
        )).apply {
            setFontScale(2f)
            setAlignment(Align.center)
        }
    }.toMap()

    var menuReady: Boolean = false

    var selectedOption: MenuItems = MenuItems.first
    set(value) {

        menuOptions[field]?.color = Color.YELLOW
        menuOptions[value]?.color = Color.BLUE
        field = value
    }

    init {

        textsGroup.addAction(Actions.delay(appearDelay,
                Actions.run {
                    menuOptions.keys
                            .sortedBy { it.order }
                            .mapNotNull { menuOptions[it] }
                            .forEach {
                                textsGroup.addActor(it)
                            }

                    textsGroup.space(SPACING)
                    menuReady = true
                }
        ))
        this.addActor(textsGroup)
        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                if (!menuReady) return true

                when (keycode) {

                    Input.Keys.ENTER -> {
                        EventSystem.submitEvent(GameEvent(GameEventTypes.MENU_OPTION_SELECTED, selectedOption))
                        return true
                    }
                    Input.Keys.UP -> {
                        selectedOption = selectedOption++
                    }
                    Input.Keys.DOWN -> {
                        selectedOption = selectedOption--
                    }

                    else -> {
                        println("Menu doesn't know what to do with keycode $keycode :(")
                    }
                }


                return true
            }
        })
    }

}