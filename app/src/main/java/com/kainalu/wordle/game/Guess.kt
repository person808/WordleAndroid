package com.kainalu.wordle.game

/** Represents the various letters a submitted guess is made of */
sealed class GuessResult {
  /** A letter that is incorrect */
  data class Incorrect(val letter: Char) : GuessResult()

  /** A letter that is partially correct */
  data class PartialMatch(val letter: Char) : GuessResult()

  /** A letter that is correct */
  data class Correct(val letter: Char) : GuessResult()
}

sealed class Guess

data class UnsubmittedGuess(private val maxSize: Int, private val value: String = "") :
  Guess(), CharSequence by value, Iterable<Char> by value.asIterable() {

  init {
    check(length <= maxSize)
    check(value.all { it.isLowerCase() })
  }

  fun insert(letter: Char): UnsubmittedGuess {
    if (!isFull()) {
      return copy(value = value + letter.lowercase())
    }
    return this
  }

  fun delete(): UnsubmittedGuess {
    if (value.isNotEmpty()) {
      return copy(value = value.dropLast(1))
    }
    return this
  }

  fun isFull(): Boolean = length == maxSize
}

data class SubmittedGuess(private val guessResults: List<GuessResult> = listOf()) :
  Guess(), Iterable<GuessResult> by guessResults
