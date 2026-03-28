package com.kainalu.wordle.game

import com.kainalu.wordle.settings.GameSettings
import java.time.LocalDate

sealed interface GameState {
  /** Settings for the game */
  val settings: GameSettings
}

sealed interface GameData : GameState {
  /** The answer to the game */
  val answer: String

  /** The guesses the player has made */
  val guesses: List<Guess>

  /** A map of guessed characters to the best [GuessResult] result for that letter */
  val guessResults: Map<Char, GuessResult>

  /** The date of the game */
  val date: LocalDate
}

data class Loading(override val settings: GameSettings) : GameState

data class Active(
  override val settings: GameSettings,
  override val answer: String,
  override val guesses: List<Guess> = emptyList(),
  override val guessResults: Map<Char, GuessResult> = emptyMap(),
  override val date: LocalDate,
) : GameData

data class Finished(
  override val settings: GameSettings,
  override val answer: String,
  override val guesses: List<Guess> = emptyList(),
  override val guessResults: Map<Char, GuessResult> = emptyMap(),
  override val date: LocalDate,
) : GameData
