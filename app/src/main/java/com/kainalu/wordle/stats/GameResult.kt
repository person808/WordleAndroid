package com.kainalu.wordle.stats

import java.time.LocalDate

data class GameResult(
    val date: LocalDate,
    val numGuesses: Int,
    val won: Boolean,
)
