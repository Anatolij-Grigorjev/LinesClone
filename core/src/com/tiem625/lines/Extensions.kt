package com.tiem625.lines

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

