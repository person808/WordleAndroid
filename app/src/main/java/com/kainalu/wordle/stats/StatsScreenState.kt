package com.kainalu.wordle.stats

sealed class StatsScreenState {
    object Loading : StatsScreenState()
    data class Loaded(
        val gamesPlayed: Int,
        val winRate: Double,
        val currentWinStreak: Int,
        val maxWinStreak: Int,
        val gameDistribution: Map<Int, Int>,
    ) : StatsScreenState()
}