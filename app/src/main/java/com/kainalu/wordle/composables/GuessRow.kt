package com.kainalu.wordle.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kainalu.wordle.game.Guess
import com.kainalu.wordle.game.GuessResult
import com.kainalu.wordle.game.SubmittedGuess
import com.kainalu.wordle.game.UnsubmittedGuess
import com.kainalu.wordle.settings.WORD_SIZE
import kotlin.collections.forEach
import kotlin.collections.zip

@Composable
fun GuessRow(modifier: Modifier = Modifier, guess: Guess?) {
  val flipAnimation = remember { FlipAnimation() }
  val winnerAnimation = remember { WinnerAnimation() }

  LaunchedEffect(guess) {
    if (guess is SubmittedGuess) {
      flipAnimation.play()
      if (guess.isCorrect()) {
        winnerAnimation.play()
      }
    }
  }

  Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    when (guess) {
      null -> repeat(WORD_SIZE) { EmptySpace() }
      is UnsubmittedGuess -> {
        guess.forEach { letter -> GuessSpace(letter = letter) }
        // Pad guess row with empty spaces
        repeat(WORD_SIZE - guess.length) { EmptySpace() }
      }
      is SubmittedGuess -> {
        guess
          .zip(flipAnimation.values)
          .zip(winnerAnimation.values) { (a, b), c -> Triple(a, b, c) }
          .forEach { (guessResult, flipAnimation, winnerAnimation) ->
            when (guessResult) {
              is GuessResult.Correct ->
                CorrectSpace(
                  modifier = Modifier.then(flipAnimation.modifier).then(winnerAnimation.modifier),
                  letter = guessResult.letter,
                  showResult = flipAnimation.showResult.value,
                )
              is GuessResult.PartialMatch ->
                PartialMatchSpace(
                  modifier = flipAnimation.modifier,
                  letter = guessResult.letter,
                  showResult = flipAnimation.showResult.value,
                )
              is GuessResult.Incorrect ->
                IncorrectSpace(
                  modifier = flipAnimation.modifier,
                  letter = guessResult.letter,
                  showResult = flipAnimation.showResult.value,
                )
            }
          }
      }
    }
  }
}
