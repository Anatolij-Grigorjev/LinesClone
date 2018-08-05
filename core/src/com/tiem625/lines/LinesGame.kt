package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.tiem625.lines.assets.Assets
import com.tiem625.lines.stages.TilesGrid

class LinesGame : ApplicationAdapter() {

    companion object {
        const val WORLD_WIDTH = 640.0f
        const val WORDL_HEIGHT = 480.0f

        const val GRID_ROWS = 8;
        const val GRID_COLS = 8;
    }

    lateinit var tilesGrid: TilesGrid

    //all ball positions used during the game. when this list runs out, its over
    //disappearing groups add their positions back into this
    val ballPositions = (0..GRID_ROWS).map { rIdx ->
        (0..GRID_COLS).map { cIdx ->
            rIdx to cIdx
        }
    }.flatten().toMutableList().shuffled()

    override fun create() {

        Assets.load()
        tilesGrid = TilesGrid(GRID_ROWS, GRID_COLS)
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
