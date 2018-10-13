package com.tiem625.lines.stages

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.constants.GameScreens
import com.tiem625.lines.constants.OptionsItems
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import com.tiem625.lines.toOptionsWord

class OptionsMenu(viewport: Viewport) : Stage(viewport) {

    val textsGroup = VerticalGroup()
    val LABEL_HEIGHT = 30.0f
    val SPACING = 20.0f

    val menuOptions = mapOf<OptionsItems, Actor>(
            OptionsItems.TOGGLE_MUSIC to toggleOptionGroup(OptionsItems.TOGGLE_MUSIC, GameRuntime.musicOn),
            OptionsItems.TOGGLE_SFX to toggleOptionGroup(OptionsItems.TOGGLE_SFX, GameRuntime.sfxOn)
    )

    private fun toggleOptionGroup(option: OptionsItems, toggleValue: Boolean): HorizontalGroup {
        return HorizontalGroup().apply {
            addActor(Label(option.menuLine, GridGlobals.gameSkin).apply {
                setFontScale(2.0f)
                height = LABEL_HEIGHT
                setAlignment(Align.left)
            })
            addActor(Label(toggleValue.toOptionsWord(), GridGlobals.gameSkin).apply {
                setFontScale(2.0f)
                height = LABEL_HEIGHT
                setAlignment(Align.right)
            })
            align(Align.center)
        }
    }

    var selectedOption: OptionsItems = OptionsItems.first
        set(value) {

            menuOptions[field]?.color = Color.YELLOW
            menuOptions[value]?.color = Color.RED
            field = value
        }

    init {

        textsGroup.addAction(
                Actions.run {
                    menuOptions.keys
                            .sortedBy { it.order }
                            .mapNotNull { menuOptions[it] }
                            .forEach {
                                textsGroup.addActor(it)
                            }

                    textsGroup.apply {
                        space(SPACING)
                        align(Align.center)
                        width = viewport.worldWidth
                        moveBy(0.0f,
                                //add half of all children height
                                (SPACING + children.fold(0f) { acc, child ->
                                    child.height + acc
                                } / 2) + SPACING
                        )
                        selectedOption = OptionsItems.first
                    }
                }
        )
        this.addActor(textsGroup)

        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                when (keycode) {

                    Input.Keys.ENTER -> {
                        EventSystem.submitEvent(GameEventTypes.OPTIONS_OPTION_SELECTED, selectedOption)
                        return true
                    }
                    Input.Keys.DOWN -> {
                        selectedOption = ++selectedOption
                    }
                    Input.Keys.UP -> {
                        selectedOption = --selectedOption
                    }
                    Input.Keys.ESCAPE -> {
                        //go to main on escape
                        EventSystem.submitEvent(GameEventTypes.STAGE_ESCAPE, GameScreens.OPTIONS)
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