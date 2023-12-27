package com.kainalu.wordle.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = lightPrimary,
    onPrimary = lightOnPrimary,
    primaryContainer = lightPrimaryContainer,
    onPrimaryContainer = lightOnPrimaryContainer,
    secondary = lightSecondary,
    onSecondary = lightOnSecondary,
    secondaryContainer = lightSecondaryContainer,
    onSecondaryContainer = lightOnSecondaryContainer,
    tertiary = lightTertiary,
    onTertiary = lightOnTertiary,
    tertiaryContainer = lightTertiaryContainer,
    onTertiaryContainer = lightOnTertiaryContainer,
    error = lightError,
    onError = lightOnError,
    errorContainer = lightErrorContainer,
    onErrorContainer = lightOnErrorContainer,
    outline = lightOutline,
    background = lightBackground,
    onBackground = lightOnBackground,
    surface = lightSurface,
    onSurface = lightOnSurface,
    surfaceVariant = lightSurfaceVariant,
    onSurfaceVariant = lightOnSurfaceVariant,
    inverseSurface = lightInverseSurface,
    inverseOnSurface = lightInverseOnSurface,
    inversePrimary = lightInversePrimary,
    surfaceTint = lightSurfaceTint,
    outlineVariant = lightOutlineVariant,
    scrim = lightScrim,
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = darkPrimary,
    onPrimary = darkOnPrimary,
    primaryContainer = darkPrimaryContainer,
    onPrimaryContainer = darkOnPrimaryContainer,
    secondary = darkSecondary,
    onSecondary = darkOnSecondary,
    secondaryContainer = darkSecondaryContainer,
    onSecondaryContainer = darkOnSecondaryContainer,
    tertiary = darkTertiary,
    onTertiary = darkOnTertiary,
    tertiaryContainer = darkTertiaryContainer,
    onTertiaryContainer = darkOnTertiaryContainer,
    error = darkError,
    onError = darkOnError,
    errorContainer = darkErrorContainer,
    onErrorContainer = darkOnErrorContainer,
    outline = darkOutline,
    background = darkBackground,
    onBackground = darkOnBackground,
    surface = darkSurface,
    onSurface = darkOnSurface,
    surfaceVariant = darkSurfaceVariant,
    onSurfaceVariant = darkOnSurfaceVariant,
    inverseSurface = darkInverseSurface,
    inverseOnSurface = darkInverseOnSurface,
    inversePrimary = darkInversePrimary,
    surfaceTint = darkSurfaceTint,
    outlineVariant = darkOutlineVariant,
    scrim = darkScrim,
  )

val MaterialTheme.guessColorsPalette: GuessColorsPalette
  @Composable @ReadOnlyComposable get() = LocalGuessColorsPalette.current

@Composable
fun WordleTheme(
  useDarkTheme: Boolean = isSystemInDarkTheme(),
  useDynamicColors: Boolean = true,
  content: @Composable () -> Unit
) {
  val colorScheme =
    when {
      useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if (useDarkTheme) dynamicDarkColorScheme(context = LocalContext.current)
        else dynamicLightColorScheme(context = LocalContext.current)
      }
      useDarkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme) {
    val customColorsPalette =
      if (useDarkTheme) {
        GuessColorsPalette.createDarkPalette(MaterialTheme.colorScheme.surfaceVariant)
      } else {
        GuessColorsPalette.createLightPalette(MaterialTheme.colorScheme.surfaceVariant)
      }

    CompositionLocalProvider(
      LocalGuessColorsPalette provides customColorsPalette,
      content = content
    )
  }
}
