package com.tiem625.lines

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import java.io.StringWriter
import java.util.*


val rnd = Random()

/**
 * Returns this mutable list, with its elements shuffled using the
 * default shuffle provided by Collections.shuffle
 */
fun <T> MutableList<T>.shuffled(): MutableList<T> {
    return this.apply {
        java.util.Collections.shuffle(this)
    }
}

/**
 * Removes amount elements from list and returns them.
 * list is modified
 */
fun <T> MutableList<T>.pop(amount: Int = 1): List<T> {
    val popped = mutableListOf<T>()
    val adjustedAmount = Math.min(amount, this.size)
    (0 until adjustedAmount).map {
        popped.add(this.removeAt(0))
    }

    return popped
}

/**
 * Fetch random element from list.
 * Throws if no elements in list
 */
fun <T> List<T>.random(): T {
    if (this.isEmpty()) {
        throw RuntimeException("No elements in list ${this}, can't random!")
    }

    return this[rnd.nextInt(this.size)]
}


operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> {
    return Pair(this.first + other.first, this.second + other.second)
}

operator fun Pair<Int, Int>.times(other: Int): Pair<Int, Int> {
    return Pair(this.first * other, this.second * other)
}

fun Pair<Int, Int>.toIndex(base: Int = 10): Int =
        first * base + second

fun Pair<Int, Int>.distanceTo(other: Pair<Int, Int>): Pair<Int, Int> =
        Pair(Math.abs(first - other.first), Math.abs(second - other.second))

fun clamp(value: Float, min: Float, max: Float): Float =
        Math.min(Math.max(value, min), max)

fun clamp(value: Int, min: Int, max: Int): Int =
        Math.min(Math.max(value, min), max)

fun Texture.asDrawable(region: Rectangle = Rectangle(0f, 0f, this.width.toFloat(), this.height.toFloat())) =
        TextureRegionDrawable(TextureRegion(this,
                region.x.toInt(),
                region.y.toInt(),
                region.width.toInt(),
                region.height.toInt())
        )

fun Button.click() {
    this.fire(InputEvent().apply {
        relatedActor = this@click
        type = InputEvent.Type.touchDown
    })
    this.fire(InputEvent().apply {
        relatedActor = this@click
        type = InputEvent.Type.touchUp
    })
}

fun Music.isNotPlaying() = !isPlaying

fun Boolean.toOptionsWord() = if (this) "YES" else "NO"

fun writeJSONFile(filename: String, storeDataActions: (JsonWriter) -> Unit) {

    val dataFile = Gdx.files.local(filename)
    dataFile.writeString(
            JsonWriter(StringWriter())
                    .apply(storeDataActions)
                    .writer.toString(),
            false,
            Charsets.UTF_8.displayName()
    )
}

fun <T> readJSONFile(filename: String, foundDataActions: (JsonValue) -> T): T? {

    val dataFile = Gdx.files.local(filename)

    if (!dataFile.exists()) return null

    return try {
        dataFile.readString(Charsets.UTF_8.displayName()).let {
            JsonReader().parse(it).let(foundDataActions)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}