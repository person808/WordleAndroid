package com.kainalu.wordle.game

sealed class Event {
  data object GuessNotInWordList : Event()
  data object GuessTooShort : Event()
  data class GameFinished(val answer: String, val won: Boolean) : Event()
}
