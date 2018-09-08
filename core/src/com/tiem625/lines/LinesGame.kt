package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.stages.MainMenu
import com.tiem625.lines.stages.SplashGridStage
import com.tiem625.lines.stages.TilesGrid
import com.tiem625.lines.stages.ui.GridHUD


class LinesGame : ApplicationAdapter() {

    lateinit var tilesGrid: TilesGrid
    lateinit var splashGridStage: SplashGridStage
    lateinit var mainMenuStage: MainMenu
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

        splashGridStage = SplashGridStage(viewport)
        mainMenuStage = MainMenu(viewport,
                splashGridStage.splashMoveTime
                        + splashGridStage.midBallDelay * splashGridStage.splashBalls.size
        )
        Gdx.input.inputProcessor = mainMenuStage
        mainMenuStage.addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {

                //add leaving actions
                mainMenuStage.addAction(Actions.moveBy(0.0f, -1000f, Gdx.graphics.deltaTime * 70))
                splashGridStage.addAction(Actions.moveBy(0.0f, -1000f, Gdx.graphics.deltaTime * 70))

                createGameGrid()



                return true
            }
        })
    }



    fun createGameGrid() {

        gridHUD = GridHUD(viewport)
        tilesGrid = TilesGrid(
                viewport,
                GridGlobals.WORLD_WIDTH,
                GridGlobals.WORLD_HEIGHT,
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
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
//        tilesGrid.act(Gdx.graphics.deltaTime)
//        gridHUD.act(Gdx.graphics.deltaTime)
        splashGridStage.act(Gdx.graphics.deltaTime)
        mainMenuStage.act(Gdx.graphics.deltaTime)
//        tilesGrid.draw()
//        gridHUD.draw()
        splashGridStage.draw()
        mainMenuStage.draw()
    }

    override fun dispose() {
        tilesGrid.dispose()
        gridHUD.dispose()
        splashGridStage.dispose()
        mainMenuStage.dispose()
        Assets.manager.dispose()
        GridGlobals.dispose()
    }

    fun gameOver() {
        println("Balls done, game over...")
        Thread.sleep(1000)
        Gdx.app.exit()
    }
}
