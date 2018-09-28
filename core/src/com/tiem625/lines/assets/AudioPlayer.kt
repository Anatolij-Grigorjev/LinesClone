package com.tiem625.lines.assets

import com.tiem625.lines.constants.SoundFx

object AudioPlayer {


    fun playSfx(sound: SoundFx) {

        val asset = when (sound) {

            SoundFx.BALLS_POP -> Assets.ballsPopSfx
            SoundFx.SELECT_TILE -> Assets.selectTileSfx
        }

        asset.play()
    }
}