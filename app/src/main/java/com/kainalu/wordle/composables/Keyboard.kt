package com.kainalu.wordle.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kainalu.wordle.game.GuessResult
import com.kainalu.wordle.ui.theme.guessColorsPalette

sealed class Key {
  data class Character(val char: Char) : Key()

  data object Submit : Key()

  data object Delete : Key()
}

private val QWERTY_KEYBOARD_LAYOUT: List<List<Key>> =
  listOf(
    "qwertyuiop".map { Key.Character(it) },
    "asdfghjkl".map { Key.Character(it) },
    buildList {
      add(Key.Submit)
      addAll("zxcvbnm".map { Key.Character(it) })
      add(Key.Delete)
    }
  )

@Composable
fun Keyboard(
  modifier: Modifier = Modifier,
  layout: List<List<Key>> = QWERTY_KEYBOARD_LAYOUT,
  guessResults: Map<Char, GuessResult>,
  onKeyPress: (Key) -> Unit = {}
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    layout.forEach { row ->
      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        row.forEach { keybinding ->
          val onClick = { onKeyPress(keybinding) }
          when (keybinding) {
            is Key.Character ->
              LetterButton(
                text = keybinding.char.uppercase(),
                result = guessResults[keybinding.char],
                onClick = onClick
              )
            is Key.Submit ->
              TextButton(modifier = Modifier.width(60.dp), text = "Submit", onClick = onClick)
            is Key.Delete ->
              IconButton(
                modifier = Modifier.width(60.dp),
                icon = Icons.AutoMirrored.Outlined.Backspace,
                onClick = onClick
              )
          }
        }
      }
    }
  }
}

@Composable
private fun KeyboardButton(
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.Unspecified,
  onClick: () -> Unit = {},
  content: @Composable () -> Unit
) {
  Box(
    modifier =
      modifier
        .height(40.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(
          if (backgroundColor.isSpecified) backgroundColor
          else MaterialTheme.colorScheme.surfaceVariant
        )
        .clickable { onClick() },
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

@Composable
private fun TextButton(
  modifier: Modifier = Modifier,
  text: String,
  backgroundColor: Color = Color.Unspecified,
  textColor: Color = Color.Unspecified,
  onClick: () -> Unit = {}
) {
  KeyboardButton(
    modifier = modifier.defaultMinSize(minWidth = 24.dp),
    backgroundColor = backgroundColor,
    onClick = onClick
  ) {
    Text(text = text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
  }
}

@Composable
private fun LetterButton(text: String, result: GuessResult?, onClick: () -> Unit = {}) {
  val backgroundColor =
    when (result) {
      is GuessResult.Correct -> MaterialTheme.guessColorsPalette.correctGuessBackground
      is GuessResult.PartialMatch -> MaterialTheme.guessColorsPalette.partialMatchBackground
      is GuessResult.Incorrect -> MaterialTheme.guessColorsPalette.incorrectGuessBackground
      null -> Color.Unspecified
    }

  val textColor =
    if (result == null) {
      MaterialTheme.colorScheme.onSurfaceVariant
    } else {
      MaterialTheme.guessColorsPalette.guessText
    }

  TextButton(
    modifier = Modifier.width(28.dp),
    text = text,
    backgroundColor = backgroundColor,
    textColor = textColor,
    onClick = onClick
  )
}

@Composable
private fun IconButton(
  modifier: Modifier = Modifier,
  backgroundColor: Color = Color.Unspecified,
  icon: ImageVector,
  iconTint: Color = Color.Unspecified,
  onClick: () -> Unit = {},
) {
  KeyboardButton(
    modifier.defaultMinSize(24.dp),
    backgroundColor = backgroundColor,
    onClick = onClick
  ) {
    Icon(
      icon,
      modifier = Modifier.size(20.dp),
      contentDescription = null,
      tint = if (iconTint.isUnspecified) MaterialTheme.colorScheme.onSurfaceVariant else iconTint
    )
  }
}

private class SampleGuessResultProvider : PreviewParameterProvider<Map<Char, GuessResult>> {
  override val values =
    sequenceOf(
      emptyMap(),
      mapOf(
        'a' to GuessResult.Correct('a'),
        'g' to GuessResult.PartialMatch('g'),
        'e' to GuessResult.Incorrect('e')
      )
    )
}

@Preview
@Composable
fun KeyboardPreview(
  @PreviewParameter(SampleGuessResultProvider::class) guessResults: Map<Char, GuessResult>
) {
  MaterialTheme { Keyboard(guessResults = guessResults) }
}
