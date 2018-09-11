package com.tiem625.lines.constants

enum class MenuItems(val order: Int, val menuLine: String) {


    START_GAME(1,"Start Game"),
    VIEW_LEADERBOARDS(START_GAME.order + 1,"Leaderboards"),
    EXIT_GAME(VIEW_LEADERBOARDS.order + 1,"Exit");

    companion object {
        private val orderMap = values().map { (it.order to it) }.toMap()

        val first: MenuItems = values().minBy { it.order }!!
        val last: MenuItems = values().maxBy { it.order }!!

        fun forOrder(order: Int) = orderMap[order]
    }

    fun next(): MenuItems = forOrder(this.order + 1) ?: first
    fun prev(): MenuItems = forOrder(this.order - 1) ?: last

    operator fun inc() = next()
    operator fun dec() = prev()
}