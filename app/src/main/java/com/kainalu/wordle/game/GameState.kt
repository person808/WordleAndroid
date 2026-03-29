package com.kainalu.wordle.game

import com.kainalu.wordle.settings.GameSettings
import java.time.LocalDate

sealed interface GameState {
  /** Settings for the game */
  val settings: GameSettings
}

sealed class LoadedGame(guesses: List<Guess>) : GameState {
  /** The answer to the game */
  abstract val answer: String
  /** The guesses the player has made */
  abstract val guesses: List<Guess>
  /** A map of guessed characters to the best [GuessResult] result for that letter */
  abstract val guessResults: Map<Char, GuessResult>
  /** The date of the game */
  abstract val date: LocalDate
  /** The current guess */
  val currentGuessIndex: Int
    get() = guesses.lastIndex

  init {
    require(guesses.isNotEmpty()) { "guesses list must not be empty" }
  }
}

data class Loading(override val settings: GameSettings) : GameState

data class Active(
  override val settings: GameSettings,
  override val answer: String,
  override val guesses: List<Guess> = listOf(UnsubmittedGuess(answer.length)),
  override val guessResults: Map<Char, GuessResult> = emptyMap(),
  override val date: LocalDate,
) : LoadedGame(guesses)

data class Finished(
  override val settings: GameSettings,
  override val answer: String,
  override val guesses: List<Guess> = listOf(UnsubmittedGuess(answer.length)),
  override val guessResults: Map<Char, GuessResult> = emptyMap(),
  override val date: LocalDate,
) : LoadedGame(guesses)
