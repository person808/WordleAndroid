package com.kainalu.wordle.navigation

sealed class Screen(val route: String) {
  object Game : Screen("game")
  object Statistics : Screen("stats")
  object Help : Screen("help")
}
