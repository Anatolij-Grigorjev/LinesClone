package com.tiem625.lines.stages

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.*
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
import kotlin.reflect.KMutableProperty0

class OptionsMenu(viewport: Viewport) : Stage(viewport) {

    val textsGroup = VerticalGroup().debugAll() as VerticalGroup
    val LABEL_HEIGHT = 30.0f
    val SPACING = 20.0f
    val FONT_SCALE = 1.5f

    val menuOptions = mapOf<OptionsItems, Actor>(
            OptionsItems.TOGGLE_MUSIC to toggleOptionGroup(OptionsItems.TOGGLE_MUSIC, GameRuntime::musicOn),
            OptionsItems.TOGGLE_SFX to toggleOptionGroup(OptionsItems.TOGGLE_SFX, GameRuntime::sfxOn)
    )

    private fun toggleOptionGroup(option: OptionsItems, toggleValueProp: KMutableProperty0<Boolean>): HorizontalGroup {
        return HorizontalGroup().apply {
            userObject = toggleValueProp
            addActor(Label(option.menuLine, Label.LabelStyle(
                    GridGlobals.skinRegularFont, Color.YELLOW
            )).apply {
                setFontScale(FONT_SCALE)
                height = LABEL_HEIGHT
                setAlignment(Align.left)
            })
            addActor(Label(toggleValueProp.get().toOptionsWord(), Label.LabelStyle(
                    GridGlobals.skinRegularFont, Color.YELLOW
            )).apply {
                setFontScale(FONT_SCALE)
                height = LABEL_HEIGHT
                setAlignment(Align.right)
            })
            align(Align.center)
            height = LABEL_HEIGHT
            width = viewport.worldWidth
            space(viewport.worldWidth - children.fold(1f) {
                acc, it -> acc + it.width * FONT_SCALE
            })
            debugAll()
        }
    }

    private fun changeChildColors(option: OptionsItems, newColor: Color) {
        (menuOptions[option] as? Group)?.apply {
            children.forEach { child -> child.color = newColor }
        }
    }

    var selectedOption: OptionsItems = OptionsItems.first
        set(value) {

            changeChildColors(field, Color.YELLOW)
            changeChildColors(value, Color.RED)
            field = value
        }

    init {

        textsGroup.addAction(
                Actions.run {
                    menuOptions.keys
                            .asSequence()
                            .sortedBy { it.order }
                            .mapNotNull { menuOptions[it] }
                            .forEach {
                                textsGroup.addActor(it)
                            }

                    textsGroup.apply {
                        space(SPACING)
                        align(Align.center)
                        width = viewport.worldWidth
                        x = 0f
                        val menuHeight = ((SPACING + LABEL_HEIGHT) * textsGroup.children.size) / 2 + LABEL_HEIGHT + SPACING
                        y = viewport.worldHeight - menuHeight
                    }
                    selectedOption = OptionsItems.first
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
                    Input.Keys.LEFT -> {
                        if (selectedOption in setOf(OptionsItems.TOGGLE_MUSIC, OptionsItems.TOGGLE_SFX)) {
                            toggleBooleanProp(selectedOption)
                        }
                    }
                    Input.Keys.RIGHT -> {
                        if (selectedOption in setOf(OptionsItems.TOGGLE_MUSIC, OptionsItems.TOGGLE_SFX)) {
                            toggleBooleanProp(selectedOption)
                        }
                    }
                    Input.Keys.ESCAPE -> {
                        //go to main on escape
                        EventSystem.submitEvent(GameEventTypes.STAGE_ESCAPE, GameScreens.OPTIONS)
                    }

                    else -> {
                        println("Options doesn't know what to do with keycode $keycode :(")
                    }
                }


                return true
            }
        })
    }

    private fun toggleBooleanProp(selectedOption: OptionsItems) {

        (menuOptions[selectedOption] as? Group)?.let {
            val prop = it.userObject as KMutableProperty0<Boolean>
            val newPropVal = !prop.get()

            prop.set(newPropVal)
            (it.children.get(1) as Label).setText(newPropVal.toOptionsWord())
        }
    }

}