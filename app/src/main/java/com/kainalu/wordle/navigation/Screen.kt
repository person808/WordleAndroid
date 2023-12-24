package com.kainalu.wordle.navigation

sealed class Screen(val route: String) {
  data object Game : Screen("game")

  data object Statistics : Screen("stats")

  data object Help : Screen("help")
}
