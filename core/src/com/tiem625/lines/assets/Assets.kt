package com.tiem625.lines.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture

class Assets {

    companion object {
        val manager: AssetManager = AssetManager()

        val tile = "img/tile.png"
        val ball = "img/ball.png"

        lateinit var tileTexture: Texture
        lateinit var ballTexture: Texture

        val assetTypeMappings = mapOf(
                tile to Texture::class.java,
                ball to Texture::class.java
        )

        fun load() {
            assetTypeMappings.forEach { assetPath, klass ->
                manager.load(assetPath, klass)
            }
            manager.finishLoading()

            tileTexture = manager.get(tile)
            ballTexture = manager.get(ball)
        }
    }
}