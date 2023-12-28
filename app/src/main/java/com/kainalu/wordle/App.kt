package com.kainalu.wordle

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.kainalu.wordle.composables.AppBar
import com.kainalu.wordle.game.GameScreen
import com.kainalu.wordle.navigation.Screen
import com.kainalu.wordle.stats.StatsScreen

val LocalSnackbarHostState =
  compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Preview
@Composable
fun App() {
  val snackbarHostState = remember { SnackbarHostState() }
  val navController = rememberNavController()

  CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
    Scaffold(
      modifier = Modifier.safeDrawingPadding(),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      topBar = { AppBar(navController) }
    ) { padding ->
      NavHost(
        navController = navController,
        startDestination = Screen.Game.route,
        Modifier.padding(padding)
      ) {
        composable(Screen.Game.route) { GameScreen() }
        dialog(Screen.Statistics.route) { StatsScreen(navController) }
      }
    }
  }
}
