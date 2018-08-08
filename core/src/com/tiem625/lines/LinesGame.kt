package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
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
        tilesGrid = TilesGrid(GridConfig.GRID_ROWS, GridConfig.GRID_COLS)
        resize(INITIAL_WIDTH, INITIAL_HEIGHT)
        Gdx.input.inputProcessor = tilesGrid
        tilesGrid.addListener(object : InputListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {

                val hadBalls = tilesGrid.addNewBalls()
                if (!hadBalls) {
                    println("Done!")
                    Thread.sleep(1000)
                    Gdx.app.exit()
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
        tilesGrid.draw()
    }

    override fun dispose() {
        tilesGrid.dispose()
        Assets.manager.dispose()
    }
}
