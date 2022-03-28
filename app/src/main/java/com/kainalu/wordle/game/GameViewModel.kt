package com.kainalu.wordle.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kainalu.wordle.game.words.WordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
//    private val resultsRepository: ResultsRepository,
    private val wordsRepository: WordsRepository
) :
    ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState

    private val _gameEvents = Channel<Event>(UNLIMITED)
    val gameEvents = _gameEvents.receiveAsFlow()

    init {
        viewModelScope.launch {
            _gameState.update {
                val newState =
                    GameState.Active(answer = wordsRepository.getAnswer(), maxGuesses = MAX_GUESSES)
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
                    state.copy(guesses = buildList {
                        addAll(guesses)
                        add(UnsubmittedGuess(answer.length, letter.toString()))
                    })
                } else {
                    state.copy(guesses = updateLastGuess(guesses) { guess ->
                        guess.insert(letter)
                    })
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

                state.copy(guesses = updateLastGuess(state.guesses) { guess ->
                    guess.delete()
                })
            }
        }
    }

    fun submitAnswer() {
        viewModelScope.launch {
            _gameState.update { state ->
                if (state !is GameState.Active) {
                    return@update state
                }

                val (answer, guesses, maxGuesses, guessResults) = state
                val newGuesses = updateLastGuess(guesses) { guess ->
                    checkGuess(guess, answer)
                }
                val newGuessResults = newGuesses.lastOrNull()?.let { guess ->
                    if (guess is SubmittedGuess) {
                        guessResults.toMutableMap().apply {
                            // Loop over results of the submitted guess and update the best result per
                            // character if needed
                            guess.forEach { result ->
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
                    } else {
                        guessResults
                    }
                } ?: guessResults

                newGuesses.lastOrNull()?.let { guess ->
                    // Check if game is finished
                    if (guess is SubmittedGuess && (guess.all { it is GuessResult.Correct } || newGuesses.size == maxGuesses)) {
                        val won = guess.all { it is GuessResult.Correct }

//                        resultsRepository.saveGameResult(
//                            GameResult(
//                                numGuesses = newGuesses.size,
//                                won = won
//                            )
//                        )
//                        Timber.d("Stats: ${resultsRepository.getStatistics()}")

                        val event = Event.GameFinished(answer = answer, won = won)
                        Timber.d("Game finished: $event")
                        _gameEvents.send(event)

                        GameState.Finished(
                            answer = answer,
                            guesses = newGuesses,
                            maxGuesses = maxGuesses,
                            guessResults = newGuessResults,
                        )
                    } else {
                        state.copy(guesses = newGuesses, guessResults = newGuessResults)
                    }
                } ?: state.copy(guesses = newGuesses, guessResults = newGuessResults)
            }
        }
    }

    /**
     * Checks a guess against the given answer.
     *
     * @param guess The guess to check
     * @param answer The answer to the board
     * @return A [SubmittedGuess] if [guess] is able to be checked, otherwise return the original [guess]
     */
    private suspend fun checkGuess(guess: UnsubmittedGuess, answer: String): Guess {
        if (!guess.isFull()) {
            return guess
        } else if (!wordsRepository.getValidGuessesSet()
                .contains(guess.joinToString(separator = ""))
        ) {
            withContext(Dispatchers.Main) {
                _gameEvents.send(Event.InvalidGuess)
            }
            return guess
        }

        val results =
            mutableMapOf<Int, GuessResult>().withDefault { key -> GuessResult.Incorrect(guess[key]) }

        // Check for correct letters first
        guess.forEachIndexed { index, letter ->
            if (letter.equals(answer[index], ignoreCase = true)) {
                results[index] = GuessResult.Correct(letter)
            }
        }

        // Check for partial matches next (right letter, wrong place)
        answer.forEachIndexed outer@{ answerIndex, answerLetter ->
            guess.forEachIndexed { index, letter ->
                if (letter.equals(
                        answerLetter,
                        ignoreCase = true
                    ) && results.getValue(answerIndex) is GuessResult.Incorrect
                ) {
                    results[index] = GuessResult.PartialMatch(letter)
                    // Found a partial match, move on to checking next letter of the answer
                    return@outer
                }
            }
        }

        return SubmittedGuess(guessResults = buildList {
            repeat(answer.length) { index -> add(results.getValue(index)) }
        })
    }

    /**
     * Updates the last unsubmitted guess using the given transform function and returns the updated
     * list of guesses.
     *
     * @param guesses The list of guesses to update
     * @param transform The function to call to get the updated guess value. Only invoked if the
     *                    last guess is an [UnsubmittedGuess]
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

    companion object {
        private const val MAX_GUESSES = 6
    }
}