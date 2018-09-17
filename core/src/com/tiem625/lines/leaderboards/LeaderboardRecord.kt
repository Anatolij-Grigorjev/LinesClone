package com.tiem625.lines.leaderboards

data class LeaderboardRecord(
        val name: String,
        val score: Int
) {
    companion object {

        fun empty(): LeaderboardRecord =
                LeaderboardRecord("---", 0)
    }
}