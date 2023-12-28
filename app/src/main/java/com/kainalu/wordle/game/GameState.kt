package com.kainalu.wordle.game

import java.time.LocalDate

interface GameData {
  /** The answer to the game */
  val answer: String
  /** The guesses the player has made */
  val guesses: List<Guess>
  /** @see [com.kainalu.wordle.settings.GameSettings.maxGuesses] */
  val maxGuesses: Int
  /** A map of guessed characters to the best [GuessResult] result for that letter */
  val guessResults: Map<Char, GuessResult>
  /** The date of the game */
  val date: LocalDate
}

sealed class GameState {

  data object Loading : GameState()

  data class Active(
    override val answer: String,
    override val guesses: List<Guess> = emptyList(),
    override val maxGuesses: Int,
    override val guessResults: Map<Char, GuessResult> = emptyMap(),
    override val date: LocalDate,
  ) : GameState(), GameData

  data class Finished(
    override val answer: String,
    override val guesses: List<Guess> = emptyList(),
    override val maxGuesses: Int,
    override val guessResults: Map<Char, GuessResult> = emptyMap(),
    override val date: LocalDate,
  ) : GameState(), GameData
}
