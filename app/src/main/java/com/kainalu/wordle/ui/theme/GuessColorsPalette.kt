package com.kainalu.wordle.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb

@Immutable
data class GuessColorsPalette(
  val correctGuessBackground: Color = Color.Unspecified,
  val partialMatchBackground: Color = Color.Unspecified,
  val incorrectGuessBackground: Color = Color.Unspecified,
  val guessText: Color = Color.Unspecified
) {
  companion object {
    fun createDarkPalette(
      incorrectGuessBackgroundBase: Color = Color.Unspecified
    ): GuessColorsPalette {
      val incorrectGuessBackground =
        if (incorrectGuessBackgroundBase.isSpecified) {
          darkenColor(incorrectGuessBackgroundBase, 0.6f)
        } else {
          Color.Unspecified
        }

      return GuessColorsPalette(
        correctGuessBackground = darkCorrectGuessBackground,
        partialMatchBackground = darkPartialMatchBackground,
        incorrectGuessBackground = incorrectGuessBackground,
        guessText = darkGuessText
      )
    }

    fun createLightPalette(
      incorrectGuessBackgroundBase: Color = Color.Unspecified
    ): GuessColorsPalette {
      val incorrectGuessBackground =
        if (incorrectGuessBackgroundBase.isSpecified) {
          darkenColor(incorrectGuessBackgroundBase, 0.6f)
        } else {
          Color.Unspecified
        }

      return GuessColorsPalette(
        correctGuessBackground = lightCorrectGuessBackground,
        partialMatchBackground = lightPartialMatchBackground,
        incorrectGuessBackground = incorrectGuessBackground,
        guessText = lightGuessText
      )
    }
  }
}

fun darkenColor(color: Color, valueScaleFactor: Float): Color {
  val hsvValues =
    FloatArray(3).apply {
      android.graphics.Color.colorToHSV(color.toArgb(), this)
      this[2] *= valueScaleFactor
    }
  return Color.hsv(hue = hsvValues[0], saturation = hsvValues[1], value = hsvValues[2])
}

val lightCorrectGuessBackground = Color(0xFF6AAA64)
val lightPartialMatchBackground = Color(0xFFC9B458)
val lightGuessText = Color.White

val darkCorrectGuessBackground = Color(0xFF538D4E)
val darkPartialMatchBackground = Color(0xFFB59F3B)
val darkGuessText = Color.White

val LocalGuessColorsPalette = staticCompositionLocalOf { GuessColorsPalette() }
