package com.tiem625.lines

/**
 * Returns this mutable list, with its elements shuffled using the
 * default shuffle provided by Collections.shuffle
 */
fun <T> MutableList<T>.shuffled(): MutableList<T> {
    return this.apply {
        java.util.Collections.shuffle(this)
    }
}