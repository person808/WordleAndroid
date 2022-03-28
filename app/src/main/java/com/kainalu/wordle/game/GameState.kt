package com.kainalu.wordle.game

interface ActiveGameData {
    /** The answer to the game */
    val answer: String
    /** The guesses the player has made */
    val guesses: List<Guess>
    /** The maximum number of guesses a player has to guess the answer */
    val maxGuesses: Int
    /** A map of guessed characters to the best [GuessResult] result for that letter */
    val guessResults: Map<Char, GuessResult>
}

sealed class GameState {

    object Loading : GameState()

    data class Active(
        override val answer: String,
        override val guesses: List<Guess> = emptyList(),
        override val maxGuesses: Int,
        override val guessResults: Map<Char, GuessResult> = emptyMap()
    ) : GameState(), ActiveGameData

    data class Finished(
        override val answer: String,
        override val guesses: List<Guess> = emptyList(),
        override val maxGuesses: Int,
        override val guessResults: Map<Char, GuessResult> = emptyMap()
    ) : GameState(), ActiveGameData
}