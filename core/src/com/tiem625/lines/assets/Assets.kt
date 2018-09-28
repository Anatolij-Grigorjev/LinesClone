package com.tiem625.lines.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture

class Assets {

    companion object {
        val manager: AssetManager = AssetManager()
        //IMAGES
        val imgPathTile = "img/tile.png"
        val imgPathBall = "img/ball.png"

        //SFX
        val sfxPathSelectTile = "sfx/select_tile.mp3"
        val sfxPathBallsPop = "sfx/balls_pop.wav"


        //texture
        lateinit var tileTexture: Texture
        lateinit var ballTexture: Texture

        lateinit var selectTileSfx: Sound
        lateinit var ballsPopSfx: Sound

        val assetTypeMappings = mapOf(
                imgPathTile to Texture::class.java,
                imgPathBall to Texture::class.java,
                sfxPathBallsPop to Sound::class.java,
                sfxPathSelectTile to Sound::class.java
        )

        fun load() {
            assetTypeMappings.forEach { assetPath, klass ->
                manager.load(assetPath, klass)
            }
            manager.finishLoading()

            tileTexture = manager.get(imgPathTile)
            ballTexture = manager.get(imgPathBall)

            selectTileSfx = manager.get(sfxPathSelectTile)
            ballsPopSfx = manager.get(sfxPathBallsPop)
        }

        fun dispose() {

            manager.dispose()
            tileTexture.dispose()
            ballTexture.dispose()
            ballsPopSfx.dispose()
            selectTileSfx.dispose()
        }
    }
}