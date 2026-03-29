package com.kainalu.wordle.settings

const val WORD_SIZE = 5

data class GameSettings(
  /** The maximum number of guesses a player has to guess the answer */
  val maxGuesses: Int
)
