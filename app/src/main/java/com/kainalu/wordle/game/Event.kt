package com.kainalu.wordle.game

sealed class Event {
    object GuessNotInWordList : Event()
    object GuessTooShort : Event()
    data class GameFinished(val answer: String, val won: Boolean) : Event()
}