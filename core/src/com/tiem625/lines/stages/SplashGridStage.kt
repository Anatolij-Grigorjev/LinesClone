package com.tiem625.lines.stages

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.GridGlobals
import com.tiem625.lines.actors.Ball
import com.tiem625.lines.assets.AudioPlayer
import com.tiem625.lines.event.EventSystem
import com.tiem625.lines.event.GameEventTypes


/**
 * Splash-screen lines grid. Draws this:
 *
 * <code>
 * 000000000000000000000000
 * 010000100100100111001110
 * 010000100100100100001000
 * 010000100110100111000100
 * 010000100101100100000010
 * 011100100100100111001110
 * 000000000000000000000000
 * </code>
 *
 * With grid parameters defined by viewport in a way that
 * keeps it in the center, taking up approx 60% of screen
 * height
 */
class SplashGridStage(
        viewport: Viewport
) : TilesGridStage(
        viewport,
        GridGlobals.GRID_WIDTH,
        viewport.worldHeight * 0.6f,
        7,
        25,
        //remaining 0.4f of viewport height divided by 2, to center vertically
        ((viewport.worldWidth - GridGlobals.GRID_WIDTH) / 2 to viewport.worldHeight * 0.2f)) {

    val splashMoveTime = 1.5f

    var splashAnimating = false

    val splashBalls = listOf(
            //L
            (1 to 1),
            (2 to 1),
            (3 to 1),
            (4 to 1),
            (5 to 1),
            (5 to 2),
            (5 to 3),
            //I
            (1 to 6),
            (2 to 6),
            (3 to 6),
            (4 to 6),
            (5 to 6),
            //N
            (1 to 9),
            (2 to 9),
            (3 to 9),
            (4 to 9),
            (5 to 9),

            (3 to 10),
            (4 to 11),

            (1 to 12),
            (2 to 12),
            (3 to 12),
            (4 to 12),
            (5 to 12),
            //E
            (1 to 15),
            (2 to 15),
            (3 to 15),
            (4 to 15),
            (5 to 15),

            (1 to 16),
            (1 to 17),

            (3 to 16),
            (3 to 17),

            (5 to 16),
            (5 to 17),
            //S
            (1 to 20),
            (2 to 20),
            (5 to 20),
            (1 to 21),
            (3 to 21),
            (5 to 21),
            (1 to 22),
            (4 to 22),
            (5 to 22)

    )

    val midBallDelay = 1f / splashBalls.size


    val sequenceAction = Actions.sequence(
            Actions.run {
                splashAnimating = true
            },
            *splashBalls.map { point ->

                Actions.delay(
                        midBallDelay,
                        Actions.run {

                            //Y coordinate is inverted between array and screen
                            grid[grid.size - (point.first + 1)][point.second].ball = Ball(
                                    tileWidth,
                                    tileHeight,
                                    Color.YELLOW,
                                    point.first,
                                    point.second
                            )
                        }
                )
            }.toTypedArray(),
            Actions.run {
                //move the splash up when the balls are ready
                this@SplashGridStage.addAction(Actions.sequence(
                        Actions.moveBy(
                                0.0f,
                                offset.second / 2,
                                splashMoveTime,
                                Interpolation.bounceOut
                        ), Actions.run {
                    splashAnimating = false
                }))
            }
    )


    init {
        AudioPlayer.stopMusic()
        //create drawing with positions list
        gridGroup.addAction(sequenceAction)

        //replace input listener
        removeListener(gameGridInputListener)
        addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                return when (keycode) {

                    Input.Keys.ESCAPE -> {
                        if (splashAnimating) {

                            EventSystem.submitEvent(GameEventTypes.SKIP_INTRO)

                            return true
                        } else {
                            return false
                        }
                    }
                    else -> false
                }

            }
        })

    }

    fun skip() {
        println("Stopping splash...")

        //stop action
        gridGroup.removeAction(sequenceAction)

        //clear grid
        clearTilesGrid()

        //set all ball points at once
        splashBalls.forEach { point ->
            //Y coordinate is inverted between array and screen
            grid[grid.size - (point.first + 1)][point.second].ball = Ball(
                    tileWidth,
                    tileHeight,
                    Color.YELLOW,
                    point.first,
                    point.second
            )
        }

        //move stage where it needs to be
        this@SplashGridStage.addAction(Actions.moveTo(
                0f,
                offset.second * 0.5f)
        )

        //unset the animating flag
        splashAnimating = false
    }

    private fun clearTilesGrid() {

        grid.forEach { gridRow ->
            gridRow.forEach { gridCell ->
                gridCell.ball = null
            }
        }
    }
}