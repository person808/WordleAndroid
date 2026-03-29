package com.kainalu.wordle.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kainalu.wordle.ui.theme.guessColorsPalette

private val SPACE_SIZE = 48.dp
private val BORDER_WIDTH = 2.dp

@Composable
private fun SpaceBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Box(modifier = modifier.size(SPACE_SIZE), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun SpaceLetter(letter: Char, color: Color) {
  Text(
    text = letter.uppercase(),
    style = MaterialTheme.typography.headlineSmall,
    fontSize = 20.sp,
    fontWeight = FontWeight.Bold,
    color = color,
  )
}

@Preview
@Composable
fun EmptySpace() {
  SpaceBox(
    modifier =
      Modifier.background(MaterialTheme.colorScheme.surface)
        .border(BORDER_WIDTH, MaterialTheme.colorScheme.outlineVariant)
  ) {}
}

@Composable
private fun SubmittedSpace(
  modifier: Modifier = Modifier,
  letter: Char,
  showResult: Boolean,
  resultBackground: Color,
) {
  if (showResult) {
    SpaceBox(modifier = modifier.background(resultBackground)) {
      SpaceLetter(letter = letter, color = MaterialTheme.guessColorsPalette.guessText)
    }
  } else {
    GuessSpace(modifier = modifier, letter = letter)
  }
}

@Composable
fun IncorrectSpace(modifier: Modifier = Modifier, letter: Char, showResult: Boolean) {
  SubmittedSpace(
    modifier = modifier,
    letter = letter,
    showResult = showResult,
    resultBackground = MaterialTheme.guessColorsPalette.incorrectGuessBackground,
  )
}

@Composable
fun PartialMatchSpace(modifier: Modifier = Modifier, letter: Char, showResult: Boolean) {
  SubmittedSpace(
    modifier = modifier,
    letter = letter,
    showResult = showResult,
    resultBackground = MaterialTheme.guessColorsPalette.partialMatchBackground,
  )
}

@Composable
fun CorrectSpace(modifier: Modifier = Modifier, letter: Char, showResult: Boolean) {
  SubmittedSpace(
    modifier = modifier,
    letter = letter,
    showResult = showResult,
    resultBackground = MaterialTheme.guessColorsPalette.correctGuessBackground,
  )
}

@Composable
fun GuessSpace(modifier: Modifier = Modifier, letter: Char) {
  SpaceBox(
    modifier =
      modifier
        .background(MaterialTheme.colorScheme.surface)
        .border(BORDER_WIDTH, MaterialTheme.colorScheme.outline)
  ) {
    SpaceLetter(letter = letter, color = MaterialTheme.colorScheme.onSurface)
  }
}

@Preview
@Composable
fun IncorrectSpacePreview() {
  MaterialTheme { IncorrectSpace(letter = 'a', showResult = true) }
}

@Preview
@Composable
fun PartialMatchSpacePreview() {
  MaterialTheme { PartialMatchSpace(letter = 'a', showResult = true) }
}

@Preview
@Composable
fun CorrectSpacePreview() {
  MaterialTheme { CorrectSpace(letter = 'a', showResult = true) }
}

@Preview
@Composable
fun GuessSpacePreview() {
  MaterialTheme { GuessSpace(letter = 'a') }
}
