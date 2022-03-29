package com.kainalu.wordle.game

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kainalu.wordle.LocalSnackbarHostState
import com.kainalu.wordle.composables.*
import timber.log.Timber

@Preview(showBackground = true)
@Composable
fun GameScreen(modifier: Modifier = Modifier, viewModel: GameViewModel = viewModel()) {
    val state by viewModel.gameState.collectAsState()
    val snackbarState = LocalSnackbarHostState.current

    LaunchedEffect(viewModel.gameEvents) {
        viewModel.gameEvents.collect { event ->
            Timber.d("Received Event $event")
            when (event) {
                is Event.InvalidGuess -> snackbarState.showSnackbar("Not in word list")
                is Event.GameFinished -> snackbarState.showSnackbar(if (event.won) "You win!" else event.answer.uppercase())
            }
        }
    }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Need to rebind state to let compiler smart cast state type
        state.let { state ->
            if (state is GameData) {
                GameBoard(
                    modifier = Modifier.padding(16.dp),
                    guesses = state.guesses,
                    maxGuesses = state.maxGuesses,
                    wordSize = state.answer.length
                )
                Keyboard(
                    modifier = Modifier.padding(16.dp),
                    guessResults = state.guessResults
                ) { key ->
                    when (key) {
                        is Key.Character -> viewModel.guessLetter(key.char)
                        is Key.Delete -> viewModel.deleteLetter()
                        is Key.Submit -> viewModel.submitAnswer()
                    }
                }
            }
        }
    }
}

@Composable
fun GameBoard(
    modifier: Modifier = Modifier,
    guesses: List<Guess>,
    maxGuesses: Int,
    wordSize: Int,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        guesses.forEach { guess ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                when (guess) {
                    is UnsubmittedGuess -> {
                        guess.forEach { letter ->
                            GuessSpace(letter)
                        }
                        // Pad guess row with empty spaces
                        repeat(wordSize - guess.length) {
                            EmptySpace()
                        }
                    }
                    is SubmittedGuess -> {
                        guess.forEach { letter ->
                            when (letter) {
                                is GuessResult.Correct -> CorrectSpace(letter.letter)
                                is GuessResult.PartialMatch -> PartialMatchSpace(letter.letter)
                                is GuessResult.Incorrect -> IncorrectSpace(letter.letter)
                            }
                        }
                    }
                }

            }
        }

        // Pad board with empty guesses
        repeat(maxGuesses - guesses.size) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(wordSize) {
                    EmptySpace()
                }
            }
        }
    }
}