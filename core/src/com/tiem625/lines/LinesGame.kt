package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.stages.TilesGrid

class LinesGame : ApplicationAdapter() {

    lateinit var tilesGrid: TilesGrid

    override fun create() {

        Assets.load()
        tilesGrid = TilesGrid(GridConfig.GRID_ROWS, GridConfig.GRID_COLS)
        tilesGrid.addNewBalls()
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
