package com.tiem625.lines.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture

class Assets {

    companion object {
        val manager: AssetManager = AssetManager()
        //PATHS

        //image
        val imgPathTile = "img/tile.png"
        val imgPathBall = "img/ball.png"

        //sfx
        val sfxPathSelectTile = "sfx/select_tile.mp3"
        val sfxPathBallsPop = "sfx/balls_pop.wav"

        //music
        val musicPath1 = "music/music1.mp3"
        val musicPath2 = "music/music2.mp3"


        //RESOURCE

        lateinit var tileTexture: Texture
        lateinit var ballTexture: Texture

        lateinit var selectTileSfx: Sound
        lateinit var ballsPopSfx: Sound

        lateinit var music1: Music
        lateinit var music2: Music

        lateinit var orderedMusics: List<Music>

        val assetTypeMappings = mapOf(
                imgPathTile to Texture::class.java,
                imgPathBall to Texture::class.java,
                sfxPathBallsPop to Sound::class.java,
                sfxPathSelectTile to Sound::class.java,
                musicPath1 to Music::class.java,
                musicPath2 to Music::class.java
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

            music1 = manager.get(musicPath1)
            music2 = manager.get(musicPath2)

            orderedMusics = listOf(music1, music2)
            orderedMusics.forEach { music ->
                music.setOnCompletionListener {
                    AudioPlayer.playNextMusic()
                }
            }
        }

        fun dispose() {

            manager.dispose()
            tileTexture.dispose()
            ballTexture.dispose()
            ballsPopSfx.dispose()
            selectTileSfx.dispose()
            orderedMusics.forEach { music -> music.dispose() }
        }
    }
}