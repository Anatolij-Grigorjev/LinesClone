package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.stages.TilesGrid


class LinesGame : ApplicationAdapter() {

    lateinit var tilesGrid: TilesGrid

    val INITIAL_WIDTH = 640
    val INITIAL_HEIGHT = 640

    override fun create() {

        Assets.load()
        tilesGrid = TilesGrid(GridGlobals.GRID_ROWS, GridGlobals.GRID_COLS)
        resize(INITIAL_WIDTH, INITIAL_HEIGHT)
        //initialize grid with some stuff
        (0 until 5).forEach { tilesGrid.addNewBalls() }
        Gdx.input.inputProcessor = tilesGrid
        tilesGrid.addListener(object : InputListener() {

            override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.SPACE -> {
                        val haveBalls = tilesGrid.addNewBalls()
                        if (!haveBalls) {
                            println("Balls done, game over...")
                            Thread.sleep(1000)
                            Gdx.app.exit()
                        }
                    }
                    Input.Keys.H -> {
                        tilesGrid.toggleBallsHighlight()
                    }
                    Input.Keys.C -> {
                        tilesGrid.toggleConnectionsHighlight()
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
        tilesGrid.viewport.update(width, height, true)
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        tilesGrid.act(Gdx.graphics.deltaTime)
        tilesGrid.draw()
    }

    override fun dispose() {
        tilesGrid.dispose()
        Assets.manager.dispose()
    }
}
