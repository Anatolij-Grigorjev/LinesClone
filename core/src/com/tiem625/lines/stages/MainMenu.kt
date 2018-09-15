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
    val LABEL_HEIGHT = 30.0f
    val SPACING = 20.0f

    val menuOptions = MenuItems.values().map { option ->
        option to Label(option.menuLine.toUpperCase(), Label.LabelStyle(
                GridGlobals.skinRegularFont, Color.YELLOW
        )).apply {
            setFontScale(2f)
            setAlignment(Align.center)
            height = LABEL_HEIGHT
        }
    }.toMap()

    var menuReady: Boolean = false
    var dialogVisible = false

    var selectedOption: MenuItems = MenuItems.first
        set(value) {

            menuOptions[field]?.color = Color.YELLOW
            menuOptions[value]?.color = Color.RED
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
                    textsGroup.align(Align.center)
                    textsGroup.width = viewport.worldWidth
                    //move up by half height and pad by option height
                    textsGroup.moveBy(0.0f,
                            ((SPACING + LABEL_HEIGHT) * textsGroup.children.size) / 2 + LABEL_HEIGHT + SPACING
                    )
                    selectedOption = MenuItems.first
                    menuReady = true
                }
        ))
        this.addActor(textsGroup)
        EventSystem.addHandler(GameEventTypes.DIALOG_APPEAR) { dialogVisible = true }
        EventSystem.addHandler(GameEventTypes.DIALOG_DISMISS) { dialogVisible = false }
        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                //if there is dialog on stage we ignore input until it goes away
                if (!menuReady || dialogVisible) return true

                when (keycode) {

                    Input.Keys.ENTER -> {
                        EventSystem.submitEvent(GameEventTypes.MENU_OPTION_SELECTED, selectedOption)
                        return true
                    }
                    Input.Keys.DOWN -> {
                        selectedOption = ++selectedOption
                    }
                    Input.Keys.UP -> {
                        selectedOption = --selectedOption
                    }
                    Input.Keys.ESCAPE -> {
                        //simulate exit selected on escape
                        EventSystem.submitEvent(GameEventTypes.MENU_OPTION_SELECTED, MenuItems.EXIT_GAME)
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