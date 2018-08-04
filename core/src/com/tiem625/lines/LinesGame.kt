package com.tiem625.lines

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.tiem625.lines.assets.Assets

class LinesGame : ApplicationAdapter() {
    lateinit var batch: SpriteBatch

    override fun create() {
        batch = SpriteBatch()

    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()

        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        Assets.manager.dispose()
    }
}
