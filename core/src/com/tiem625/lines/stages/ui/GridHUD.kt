package com.tiem625.lines.stages.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.ui.GridHUDBackground
import com.tiem625.lines.actors.ui.MultiplierProgressBar
import com.tiem625.lines.assets.AudioPlayer
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes
import java.text.DecimalFormat

class GridHUD(viewport: Viewport) : Stage(viewport) {

    val pointsFormat = DecimalFormat("0000")
    val multiplierFormat = DecimalFormat("x0.0")

    val multiplierFieldWidth = 100.0f
    val musicStateFieldWith = 100.0f

    val backgroundGroup = Group().apply {
        x = 0.0f
        y = viewport.worldHeight - GridGlobals.HUD_HEIGHT
        width = viewport.worldWidth
        height = GridGlobals.HUD_HEIGHT

        this@GridHUD.addActor(this)
    }

    val background = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            width = viewport.worldWidth,
            height = GridGlobals.HUD_HEIGHT,
            color = Color.BROWN
    ).apply {
        backgroundGroup.addActor(this)
    }

    val pointsPadding = 15.0f

    val pointsGroup = Group().apply {
        x = pointsPadding + multiplierFieldWidth + pointsPadding
        y = pointsPadding
        //padded left and right
        width = viewport.worldWidth - 3 * pointsPadding - multiplierFieldWidth
        //padded up and down
        height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding

        backgroundGroup.addActor(this)
    }
    val pointsBg = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            //padded left and right
            width = viewport.worldWidth - 3 * pointsPadding - multiplierFieldWidth,
            //padded up and down
            height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding,
            color = Color.BLACK
    ).apply {
        pointsGroup.addActor(this)
    }
    val pointsLabel = Label("0000", Label.LabelStyle(
            GridGlobals.skinRegularFont,
            Color.WHITE)).apply {
        setAlignment(Align.right)
        pointsGroup.addActor(this)
        setFontScale(
                parent.width / width / 5, parent.height / height / 3
        )
        //center by Y with X in the right align
        x = parent.width - width - pointsPadding
        y = parent.height / 2 - height / 2
    }

    val musicStateGroup = Group().apply {
        x = pointsPadding
        y = pointsPadding
        //specific width
        width = musicStateFieldWith
        //padded up and down
        height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding

        backgroundGroup.addActor(this)
    }

    val multiplierGroup = Group().apply {
        x = 2 * pointsPadding + musicStateFieldWith
        y = pointsPadding
        //specific width
        width = multiplierFieldWidth
        //padded up and down
        height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding

        backgroundGroup.addActor(this)
    }
    val musicStateBg = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            //specific width
            width = musicStateFieldWith,
            //padded up and down
            height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding,
            color = Color.BLACK
    ).apply {
        musicStateGroup.addActor(this)
    }
    val multiplierBg = GridHUDBackground(
            x = 0.0f,
            y = 0.0f,
            //specific width
            width = multiplierFieldWidth,
            //padded up and down
            height = GridGlobals.HUD_HEIGHT - 2 * pointsPadding,
            color = Color.BLACK
    ).apply {
        multiplierGroup.addActor(this)
    }
    val musicStateLabel = Label("M", Label.LabelStyle(
            GridGlobals.skinRegularFont,
            Color.WHITE)).apply {
        setAlignment(Align.center)
        musicStateGroup.addActor(this)
        setFontScale(
                GameRuntime.multiplierScale(parent.height / height / 2)
        )
        //center by Y and X
        x = parent.width / 2 - width / 2
        y = parent.height / 2 - height / 2
    }
    val multiplierLabel = Label("x0.0", Label.LabelStyle(
            GridGlobals.skinRegularFont,
            Color.WHITE)).apply {
        setAlignment(Align.center)
        multiplierGroup.addActor(this)
        setFontScale(
                GameRuntime.multiplierScale(parent.height / height / 2)
        )
        //center by Y and X
        x = parent.width / 2 - width / 2
        y = parent.height / 2 - height / 2
    }

    val eventHandlerKeys = mutableListOf<String>()

    val multiplierProgressBar: MultiplierProgressBar = MultiplierProgressBar(
            0f,
            0f,
            viewport.worldWidth - GridGlobals.GRID_WIDTH,
            viewport.worldHeight - GridGlobals.HUD_HEIGHT
    )

    init {

        updatePointsLabel()
        updateMultiplierLabel()
        updateMusicLabel()

        addActor(multiplierProgressBar)

        eventHandlerKeys.add(EventSystem.addHandler(GameEventTypes.RECEIVE_POINTS) { event ->

            val pointsInfo = event.data as GameRuntime.PointsChange

            updatePointsLabel(pointsInfo.newPoints)
            updateProgressBar(pointsInfo.delta)
        })
        eventHandlerKeys.add(EventSystem.addHandler(GameEventTypes.CHANGE_MULTIPLIER) { event -> updateMultiplierLabel() })
        eventHandlerKeys.add(EventSystem.addHandler(GameEventTypes.USED_MUSIC_CONTROLS) { event -> updateMusicLabel() })
        eventHandlerKeys.add(EventSystem.addHandler(GameEventTypes.SCORE_PROGRESS_FULL) {event -> freezeLargeMultiplier() })

        isDebugAll = true
    }

    private fun freezeLargeMultiplier() {

        GameRuntime.currentPointsMultiplier = GridGlobals.FREEZ_MULTIPLIER_VALUE
        updateMultiplierLabel()
        multiplierLabel.color = Color.WHITE
        multiplierBg.color = Color.BLUE
    }

    private fun updateProgressBar(pointsDelta: Int) {
        multiplierProgressBar.addProgress(pointsDelta.toFloat())
    }

    private fun updatePointsLabel(newValue: Int = GameRuntime.currentPoints) {

        pointsLabel.setText(pointsFormat.format(newValue))
    }

    private fun updateMultiplierLabel() {
        multiplierLabel.setText(multiplierFormat.format(GameRuntime.currentPointsMultiplier))
    }

    private fun updateMusicLabel() {

        musicStateLabel.setText(
                //if music is stopped show nothing,
                //if its playing show M
                //if its paused show P
                when {
                    AudioPlayer.isMusicPlaying() -> "M"
                    AudioPlayer.isMusicPaused() -> "P"
                    else -> ""
                }
        )

    }

    override fun dispose() {
        eventHandlerKeys.forEach { EventSystem.removeHandler(it) }
        super.dispose()
    }
}