package com.tiem625.lines.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import net.dermetfan.gdx.assets.AnnotationAssetManager.Asset

class Assets {

    companion object {
        val manager: AssetManager = AssetManager()

        @Asset(Texture::class)
        val tile = "img/badlogic.jpg"
    }
}