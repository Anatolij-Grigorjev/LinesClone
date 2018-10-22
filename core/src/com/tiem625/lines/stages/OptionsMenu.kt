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
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.actors.Tile
import com.tiem625.lines.actors.TileBallGroup
import com.tiem625.lines.clamp
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
    val OPTION_LENGTH = viewport.worldWidth / 2

    val menuOptions = mapOf<OptionsItems, Actor>(
            OptionsItems.TOGGLE_MUSIC to toggleOptionGroup(OptionsItems.TOGGLE_MUSIC, GameRuntime::musicOn),
            OptionsItems.TOGGLE_SFX to toggleOptionGroup(OptionsItems.TOGGLE_SFX, GameRuntime::sfxOn),
            OptionsItems.NUM_BALLS to ballColorsChoiceGroup(),
            OptionsItems.EXIT to exitButtonGroup()
    )

    private fun exitButtonGroup(): HorizontalGroup {

        return HorizontalGroup().apply {
            //label
            addActor(Label(OptionsItems.EXIT.menuLine, Label.LabelStyle(
                    GridGlobals.skinRegularFont, Color.YELLOW
            )).apply {
                setFontScale(FONT_SCALE)
                height = LABEL_HEIGHT
                setAlignment(Align.center)
            })
            commonOptionsItemProps()
            align(Align.bottom)
            height = viewport.screenHeight - (OptionsItems.values().size - 1) * LABEL_HEIGHT
        }
    }

    private fun ballColorsChoiceGroup(): HorizontalGroup {

        return HorizontalGroup().apply {
            //label
            val ballsPickLabel = Label(OptionsItems.NUM_BALLS.menuLine, Label.LabelStyle(
                    GridGlobals.skinRegularFont, Color.YELLOW
            )).apply {
                setFontScale(FONT_SCALE)
                height = LABEL_HEIGHT
                setAlignment(Align.left)
            }
            addActor(ballsPickLabel)
            //balls group
            val TILE_HEIGHT = 100f
            val TILE_WIDTH = Math.min(TILE_HEIGHT, OPTION_LENGTH / GridGlobals.BALL_COLORS.size)
            val tilesLine = HorizontalGroup().apply {
                width = TILE_WIDTH * GridGlobals.BALL_COLORS.size
                height = TILE_HEIGHT
                align(Align.center)
                space(5f)
                y = TILE_HEIGHT / 2f
                debugAll()
            }
            (0 until GridGlobals.BALL_COLORS.size).map { idx ->
                TileBallGroup((0 to idx),
                        Tile(
                                TILE_WIDTH,
                                TILE_HEIGHT)
                ).apply {
                    ball = Ball(TILE_WIDTH * 0.95f,
                            TILE_HEIGHT * 0.95f,
                            GridGlobals.BALL_COLORS[idx],
                            this.gridPos.first,
                            this.gridPos.second
                    )
                    x = TILE_WIDTH * idx
                    y = TILE_HEIGHT / 2f
                    width = TILE_WIDTH
                    height = TILE_HEIGHT
                }

            }.let { groupsList ->
                //save groups in user object for easy access
                userObject = listOf(*groupsList.toTypedArray())
                groupsList
            }.forEach { group -> tilesLine.addActor(group) }
            addActor(tilesLine)
            commonOptionsItemProps()
            space(viewport.worldWidth
                    //label size
                    - (ballsPickLabel.width * FONT_SCALE)
                    //balls line size
                    - (TILE_WIDTH * GridGlobals.BALL_COLORS.size
                    //padding
                    + 20f)
            )
        }
    }

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
            commonOptionsItemProps()
        }
    }

    private fun HorizontalGroup.commonOptionsItemProps() {
        align(Align.center)
        height = LABEL_HEIGHT
        width = viewport.worldWidth
        if (children.size > 1) {
            space(viewport.worldWidth - children.fold(1f) { acc, it ->
                acc + it.width * FONT_SCALE
            })
        }
        debugAll()
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
                        align(Align.bottom)
                        width = viewport.worldWidth
                        x = 0f
                        val menuHeight = ((SPACING + LABEL_HEIGHT) * textsGroup.children.size) / 2 + LABEL_HEIGHT + SPACING
                        y = 0f
//                        y = viewport.worldHeight - menuHeight
                        height = viewport.worldHeight - menuHeight
                    }
                    selectedOption = OptionsItems.first
                }
        )
        this.addActor(textsGroup)

        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                when (keycode) {

                    Input.Keys.ENTER -> {
                        if (selectedOption == OptionsItems.EXIT) {
                            EventSystem.submitEvent(GameEventTypes.STAGE_ESCAPE, GameScreens.OPTIONS)
                        } else {
                            EventSystem.submitEvent(GameEventTypes.OPTIONS_OPTION_SELECTED, selectedOption)
                        }
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
                        if (selectedOption == OptionsItems.NUM_BALLS) {
                            changeBallCount(-1)
                        }
                    }
                    Input.Keys.RIGHT -> {
                        if (selectedOption in setOf(OptionsItems.TOGGLE_MUSIC, OptionsItems.TOGGLE_SFX)) {
                            toggleBooleanProp(selectedOption)
                        }
                        if (selectedOption == OptionsItems.NUM_BALLS) {
                            changeBallCount(1)
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
        updateBallsLine(GameRuntime.usedBallColors.size)
    }

    private fun toggleBooleanProp(selectedOption: OptionsItems) {

        (menuOptions[selectedOption] as? Group)?.let {
            val prop = it.userObject as KMutableProperty0<Boolean>
            val newPropVal = !prop.get()

            prop.set(newPropVal)
            (it.children.get(1) as Label).setText(newPropVal.toOptionsWord())
        }
    }

    private fun changeBallCount(delta: Int) {
        //ensure number of in-game balls between 2 and max colors
        val newSize = clamp(GameRuntime.usedBallColors.size + delta, 2, GridGlobals.BALL_COLORS.size)
        //clear current colors list
        GameRuntime.usedBallColors.clear()
        //re-insert needed amount
        (0 until newSize).forEach { idx -> GameRuntime.usedBallColors.add(GridGlobals.BALL_COLORS[idx]) }
        //refresh board view
        updateBallsLine(newSize)
    }

    private fun updateBallsLine(newSize: Int) {

        menuOptions[OptionsItems.NUM_BALLS]?.let {
            val tileGroupsList = it.userObject as List<TileBallGroup>
            (0 until newSize).forEach { idx ->
                tileGroupsList[idx].ball?.let { ball ->
                    if (!ball.isVisible) {
                        ball.addAction(Actions.show())
                    }
                }
            }
            (newSize until tileGroupsList.size).forEach { idx ->
                tileGroupsList[idx].ball?.let { ball ->
                    if (ball.isVisible) {
                        ball.addAction(Actions.hide())
                    }
                }
            }
        }
    }

}