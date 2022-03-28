package com.kainalu.wordle.game

sealed class Event {
    object InvalidGuess : Event()
    data class GameFinished(val answer: String, val won: Boolean) : Event()
}