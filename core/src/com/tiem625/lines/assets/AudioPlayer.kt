package com.tiem625.lines.assets

import com.badlogic.gdx.audio.Music
import com.tiem625.lines.constants.SoundFx
import com.tiem625.lines.isNotPlaying

object AudioPlayer {

    var currentPlayingMusic = 0

    fun playSfx(sound: SoundFx) {

        val asset = when (sound) {

            SoundFx.BALLS_POP -> Assets.ballsPopSfx
            SoundFx.SELECT_TILE -> Assets.selectTileSfx
        }

        asset.play()
    }

    fun playMusic() {

        withCurrentMusic {
            if (it.isNotPlaying()) {
                it.play()
            }
        }
    }

    fun pauseMusic() {

        withCurrentMusic {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun stopMusic() {
        withCurrentMusic {
            if (it.isPlaying) {
                it.stop()
            }
        }
    }

    fun isMusicPlaying(): Boolean = withCurrentMusic { it.isPlaying }

    fun playNextMusic() {

        stopMusic()
        currentPlayingMusic = (currentPlayingMusic + 1) % Assets.orderedMusics.size
        playMusic()
    }

    fun playPrevMusic() {

        stopMusic()
        currentPlayingMusic = (currentPlayingMusic - 1).let {
            if (it >= 0) it else Assets.orderedMusics.size - 1
        }
        playMusic()
    }


    private fun <T> withCurrentMusic(action: (Music) -> T): T =
            Assets.orderedMusics[currentPlayingMusic].let(action)

    //lets say music is paused if its started but not playing
    fun isMusicPaused() = withCurrentMusic { it.isNotPlaying() && it.position > 0.0f }
}