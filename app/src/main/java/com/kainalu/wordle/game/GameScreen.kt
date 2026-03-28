package com.kainalu.wordle.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kainalu.wordle.LocalSnackbarHostState
import com.kainalu.wordle.composables.CorrectSpace
import com.kainalu.wordle.composables.EmptySpace
import com.kainalu.wordle.composables.GuessSpace
import com.kainalu.wordle.composables.IncorrectSpace
import com.kainalu.wordle.composables.Key
import com.kainalu.wordle.composables.Keyboard
import com.kainalu.wordle.composables.PartialMatchSpace
import timber.log.Timber

@Composable
fun GameScreen(modifier: Modifier = Modifier, viewModel: GameViewModel = hiltViewModel()) {
  val state by viewModel.gameState.collectAsState()
  val snackbarState = LocalSnackbarHostState.current

  LaunchedEffect(viewModel.gameEvents) {
    viewModel.gameEvents.collect { event ->
      Timber.d("Received Event $event")
      when (event) {
        is Event.GuessNotInWordList -> snackbarState.showSnackbar("Not in word list")
        is Event.GuessTooShort -> snackbarState.showSnackbar("Not enough letters")
        is Event.GameFinished ->
          snackbarState.showSnackbar(if (event.won) "You win!" else event.answer.uppercase())
      }
    }
  }

  (state as? GameData)?.let { state ->
    Column(
      modifier = modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceEvenly,
    ) {
      Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        state.guesses.forEach { guess -> GuessRow(guess = guess, wordSize = state.answer.length) }

        // Pad board with empty guesses
        repeat(state.settings.maxGuesses - state.guesses.size) {
          GuessRow(guess = null, wordSize = state.answer.length)
        }
      }

      Keyboard(modifier = Modifier.padding(16.dp), guessResults = state.guessResults) { key ->
        when (key) {
          is Key.Character -> viewModel.guessLetter(key.char)
          is Key.Delete -> viewModel.deleteLetter()
          is Key.Submit -> viewModel.submitAnswer()
        }
      }
    }
  }
}

@Composable
fun GuessRow(modifier: Modifier = Modifier, guess: Guess?, wordSize: Int) {
  Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    when (guess) {
      null -> {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          repeat(wordSize) { EmptySpace() }
        }
      }
      is UnsubmittedGuess -> {
        guess.forEach { letter -> GuessSpace(letter) }
        // Pad guess row with empty spaces
        repeat(wordSize - guess.length) { EmptySpace() }
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
