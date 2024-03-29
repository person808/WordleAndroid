package com.kainalu.wordle.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kainalu.wordle.game.words.WordsRepository
import com.kainalu.wordle.settings.GameSettings
import com.kainalu.wordle.stats.GameResult
import com.kainalu.wordle.stats.ResultsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class GameViewModel
@Inject
constructor(
  private val resultsRepository: ResultsRepository,
  private val wordsRepository: WordsRepository,
  private val gameSettings: GameSettings,
) : ViewModel() {

  private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
  val gameState: StateFlow<GameState> = _gameState

  private val _gameEvents = Channel<Event>(UNLIMITED)
  val gameEvents = _gameEvents.receiveAsFlow()

  init {
    viewModelScope.launch {
      _gameState.update {
        val date = LocalDate.now()
        val newState =
          GameState.Active(
            answer = wordsRepository.getAnswer(date.toEpochDay()),
            maxGuesses = gameSettings.maxGuesses,
            date = date
          )
        Timber.d("Loaded game: $newState")
        newState
      }
    }
  }

  fun guessLetter(letter: Char) {
    viewModelScope.launch {
      _gameState.update { state ->
        if (state !is GameState.Active) {
          return@update state
        }

        val (answer, guesses) = state
        if (guesses.lastOrNull() !is UnsubmittedGuess) {
          // We need to create a new guess to fill
          state.copy(
            guesses =
              buildList {
                addAll(guesses)
                add(UnsubmittedGuess(answer.length, letter.toString().lowercase()))
              }
          )
        } else {
          state.copy(guesses = updateLastGuess(guesses) { guess -> guess.insert(letter) })
        }
      }
    }
  }

  fun deleteLetter() {
    viewModelScope.launch {
      _gameState.update { state ->
        if (state !is GameState.Active) {
          return@update state
        }

        state.copy(guesses = updateLastGuess(state.guesses) { guess -> guess.delete() })
      }
    }
  }

  fun submitAnswer() {
    viewModelScope.launch {
      _gameState.update { state ->
        if (state !is GameState.Active) {
          return@update state
        }

        val (answer, guesses, maxGuesses, guessResults, date) = state
        val newGuesses = updateLastGuess(guesses) { guess -> checkGuess(guess, answer) }
        val newGuessResults =
          newGuesses
            .lastOrNull { it is SubmittedGuess }
            ?.let { guess ->
              guessResults.toMutableMap().apply {
                // Loop over results of the submitted guess and update the best result
                // per
                // character if needed
                (guess as SubmittedGuess).forEach { result ->
                  when (result) {
                    is GuessResult.Correct -> set(result.letter, result)
                    is GuessResult.PartialMatch -> {
                      if (guessResults[result.letter] !is GuessResult.Correct) {
                        set(result.letter, result)
                      }
                    }
                    is GuessResult.Incorrect -> {
                      if (guessResults[result.letter] == null) {
                        set(result.letter, result)
                      }
                    }
                  }
                }
              }
            } ?: guessResults

        // Get final submitted guess and process it if the game is over
        newGuesses
          .lastOrNull {
            it is SubmittedGuess &&
              (it.all { result -> result is GuessResult.Correct } || newGuesses.size == maxGuesses)
          }
          ?.let { guess ->
            val won = (guess as SubmittedGuess).all { it is GuessResult.Correct }

            resultsRepository.saveGameResult(
              GameResult(date = LocalDate.now(), numGuesses = newGuesses.size, won = won)
            )
            Timber.d("Stats: ${resultsRepository.getStats()}")

            val event = Event.GameFinished(answer = answer, won = won)
            Timber.d("Game finished: $event")
            _gameEvents.send(event)

            GameState.Finished(
              answer = answer,
              guesses = newGuesses,
              maxGuesses = maxGuesses,
              guessResults = newGuessResults,
              date = date
            )
          } ?: state.copy(guesses = newGuesses, guessResults = newGuessResults)
      }
    }
  }

  /**
   * Checks a guess against the given answer.
   *
   * @param guess The guess to check
   * @param answer The answer to the board
   * @return A [SubmittedGuess] if [guess] is able to be checked, otherwise return the original
   *   [guess]
   */
  private suspend fun checkGuess(guess: UnsubmittedGuess, answer: String): Guess {
    if (!guess.isFull()) {
      _gameEvents.send(Event.GuessTooShort)
      return guess
    }

    if (!wordsRepository.getValidGuessesSet().contains(guess.joinToString(separator = ""))) {
      _gameEvents.send(Event.GuessNotInWordList)
      return guess
    }

    // Check the guess. The key rule that must be followed is that a letter in the answer may
    // only be used as a correct or partial match once. The same letter in the answer cannot be used
    // to mark multiple letters in the answer as correct or partial matches.

    // Keep track of the letters in the answer that have already been used to mark a correct or
    // partial match
    val consumedLetterIndices = mutableSetOf<Int>()
    val results =
      mutableMapOf<Int, GuessResult>().withDefault { key -> GuessResult.Incorrect(guess[key]) }

    // Check for correct letters first. We don't want to "consume" correct matches which could
    // happen if we checked for partial matches first.
    guess.forEachIndexed { index, letter ->
      if (letter.equals(answer[index], ignoreCase = true)) {
        results[index] = GuessResult.Correct(letter)
        consumedLetterIndices.add(index) // Mark letter in answer as consumed
      }
    }

    // Check for partial matches next (right letter, wrong place)
    guess.forEachIndexed { guessIndex, guessLetter ->
      if (results[guessIndex] is GuessResult.Correct) {
        return@forEachIndexed
      }

      answer.forEachIndexed { answerIndex, answerLetter ->
        // If the answer letter has not been consumed yet but still is equal to the guess letter
        // then we know its a partial match because we already processed correct matches above.
        if (
          !consumedLetterIndices.contains(answerIndex) &&
            guessLetter.equals(answerLetter, ignoreCase = true)
        ) {
          results[guessIndex] = GuessResult.PartialMatch(guessLetter)
          consumedLetterIndices.add(answerIndex) // Mark letter in answer as consumed
        }
      }
    }

    return SubmittedGuess(
      guessResults = buildList { repeat(answer.length) { index -> add(results.getValue(index)) } }
    )
  }

  /**
   * Updates the last unsubmitted guess using the given transform function and returns the updated
   * list of guesses.
   *
   * @param guesses The list of guesses to update
   * @param transform The function to call to get the updated guess value. Only invoked if the last
   *   guess is an [UnsubmittedGuess]
   */
  private suspend fun updateLastGuess(
    guesses: List<Guess>,
    transform: suspend (UnsubmittedGuess) -> Guess
  ): List<Guess> {
    return guesses.mapIndexed { index, guess ->
      if (index + 1 == guesses.size && guess is UnsubmittedGuess) {
        transform(guess)
      } else {
        guess
      }
    }
  }
}
