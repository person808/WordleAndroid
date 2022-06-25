package com.kainalu.wordle

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kainalu.wordle.composables.AppBar
import com.kainalu.wordle.game.GameScreen
import com.kainalu.wordle.navigation.Screen
import com.kainalu.wordle.stats.StatsScreen

val LocalSnackbarHostState =
    compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Preview
@Composable
fun App() {
  // Update the system bars to be translucent
  val systemUiController = rememberSystemUiController()
  val useDarkIcons = MaterialTheme.colors.isLight

  SideEffect { systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons) }

  val navController = rememberNavController()
  val scaffoldState = rememberScaffoldState()

  CompositionLocalProvider(LocalSnackbarHostState provides scaffoldState.snackbarHostState) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        scaffoldState = scaffoldState,
        topBar = { AppBar(navController) }) { padding ->
      NavHost(
          navController = navController,
          startDestination = Screen.Game.route,
          Modifier.padding(padding)) {
        composable(Screen.Game.route) { GameScreen() }
        dialog(Screen.Statistics.route) { StatsScreen(navController) }
      }
    }
  }
}
