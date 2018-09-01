package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.stages.SplashGridStage
import com.tiem625.lines.stages.TilesGrid
import com.tiem625.lines.stages.ui.GridHUD


class LinesGame : ApplicationAdapter() {

    lateinit var tilesGrid: TilesGrid
    lateinit var splashGridStage: SplashGridStage
    lateinit var gridHUD: GridHUD
    lateinit var viewport: Viewport

    companion object {
        lateinit var currentGame: LinesGame
    }

    override fun create() {

        currentGame = this

        viewport = FitViewport(
                GridGlobals.WORLD_WIDTH + Math.abs(GridGlobals.WORLD_OFFSET.first),
                GridGlobals.WORLD_HEIGHT + Math.abs(GridGlobals.WORLD_OFFSET.second))

        Assets.load()

        gridHUD = GridHUD(viewport)
        tilesGrid = TilesGrid(
                viewport,
                GridGlobals.GRID_ROWS,
                GridGlobals.GRID_COLS,
                GridGlobals.WORLD_OFFSET
        )

        //initialize grid with some stuff
        (0 until 1).forEach { tilesGrid.addNewBalls() }

        Gdx.input.inputProcessor = tilesGrid
        tilesGrid.addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.SPACE -> {
                        val haveBalls = tilesGrid.addNewBalls()
                        if (!haveBalls) {
                            gameOver()
                        }
                    }
                    else -> {
                        println("No handler for key $keycode")
                    }
                }

                return true
            }
        })

        splashGridStage = SplashGridStage(
                viewport,
                -350.0f
        )
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//        tilesGrid.act(Gdx.graphics.deltaTime)
//        gridHUD.act(Gdx.graphics.deltaTime)
//        tilesGrid.draw()
//        gridHUD.draw()
        splashGridStage.draw()
    }

    override fun dispose() {
        tilesGrid.dispose()
        gridHUD.dispose()
        splashGridStage.dispose()
        Assets.manager.dispose()
        GridGlobals.dispose()
    }

    fun gameOver() {
        println("Balls done, game over...")
        Thread.sleep(1000)
        Gdx.app.exit()
    }
}
