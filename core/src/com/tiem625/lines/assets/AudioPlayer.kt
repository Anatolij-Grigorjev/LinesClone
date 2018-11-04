package com.tiem625.lines.assets

import com.badlogic.gdx.audio.Music
import com.tiem625.lines.GameRuntime
import com.tiem625.lines.clamp
import com.tiem625.lines.constants.SoundFx
import com.tiem625.lines.isNotPlaying

object AudioPlayer {

    var currentPlayingMusic = 0

    fun playSfx(sound: SoundFx, multiplier: Float = 1f) {

        if (!GameRuntime.sfxOn) return

        val asset = when (sound) {

            SoundFx.BALLS_POP -> Assets.ballsPopSfx
            SoundFx.SELECT_TILE -> Assets.selectTileSfx
        }

        val soundId = asset.play()
        if (multiplier > 1f) {
            val addedPitch = clamp(1f + (multiplier - 1) / 2, 0.5f, 2f)
            asset.setPitch(soundId, addedPitch)
            println("Playing sound id $soundId at pitch $addedPitch")
        }

    }

    fun playMusic() {

        if (!GameRuntime.musicOn) return

        println("Starting to play music idx $currentPlayingMusic...")

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