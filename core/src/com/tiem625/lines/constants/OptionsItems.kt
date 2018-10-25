package com.tiem625.lines.constants

enum class OptionsItems(val order: Int, val menuLine: String, val configName: String) {


    TOGGLE_MUSIC(1,"MUSIC: ", "toggle_music"),
    TOGGLE_SFX(TOGGLE_MUSIC.order + 1,"SFX: ", "toggle_sfx"),
    NUM_BALLS(TOGGLE_SFX.order + 1,"Ball Colors: ", "num_balls"),
    EXIT(NUM_BALLS.order + 1, "BACK TO MAIN", "");

    companion object {
        private val orderMap = values().map { (it.order to it) }.toMap()

        val first: OptionsItems = values().minBy { it.order }!!
        val last: OptionsItems = values().maxBy { it.order }!!

        fun forOrder(order: Int) = orderMap[order]
    }

    fun next(): OptionsItems = forOrder(this.order + 1) ?: first
    fun prev(): OptionsItems = forOrder(this.order - 1) ?: last

    operator fun inc() = next()
    operator fun dec() = prev()
}