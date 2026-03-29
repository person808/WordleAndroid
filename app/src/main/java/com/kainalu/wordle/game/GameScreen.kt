package com.kainalu.wordle.game

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kainalu.wordle.LocalSnackbarHostState
import com.kainalu.wordle.R
import com.kainalu.wordle.composables.GuessRow
import com.kainalu.wordle.composables.Key
import com.kainalu.wordle.composables.Keyboard
import kotlinx.coroutines.launch
import timber.log.Timber

val shakeAnimationValues = floatArrayOf(0f, -8f, 8f, -16f, 16f, -4f, 0f)
val shakeAnimationSpec = tween<Float>(30)

suspend fun playShakeAnimation(animatable: Animatable<Float, *>) {
  shakeAnimationValues.forEach { animatable.animateTo(it, shakeAnimationSpec) }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun GameScreen(modifier: Modifier = Modifier, viewModel: GameViewModel = hiltViewModel()) {
  val state by viewModel.gameState.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val snackbarState = LocalSnackbarHostState.current
  val rowAnimatables = remember { List(state.settings.maxGuesses) { Animatable(0f) } }

  fun showGuessError(message: String) {
    scope.launch {
      snackbarState.currentSnackbarData?.dismiss()
      snackbarState.showSnackbar(message)
    }
    (state as? LoadedGame)?.let { state ->
      scope.launch { playShakeAnimation(rowAnimatables[state.currentGuessIndex]) }
    }
  }

  LaunchedEffect(viewModel.gameEvents) {
    viewModel.gameEvents.collect { event ->
      Timber.d("Received Event $event")
      when (event) {
        is Event.GuessNotInWordList ->
          showGuessError(context.getString(R.string.guess_not_in_word_list))
        is Event.GuessTooShort -> showGuessError(context.getString(R.string.guess_too_short))
        // FIXME: The "You win!" message shows before the reveal animation completes
        is Event.GameFinished ->
          snackbarState.showSnackbar(
            if (event.won) context.getString(R.string.you_win) else event.answer.uppercase()
          )
      }
    }
  }

  (state as? LoadedGame)?.let { state ->
    Column(
      modifier = modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceEvenly,
    ) {
      Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        state.guesses.zip(rowAnimatables).forEach { (guess, animatable) ->
          GuessRow(
            modifier = Modifier.graphicsLayer { translationX = animatable.value },
            guess = guess,
          )
        }

        // Pad board with empty guesses
        repeat(state.settings.maxGuesses - state.guesses.size) { GuessRow(guess = null) }
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
