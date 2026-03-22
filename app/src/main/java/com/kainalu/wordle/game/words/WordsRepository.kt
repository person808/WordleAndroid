package com.kainalu.wordle.game.words

import com.kainalu.wordle.persistence.ValidAnswersDao
import com.kainalu.wordle.persistence.ValidGuessesDao
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WordsRepository
@Inject
constructor(private val answersDao: ValidAnswersDao, private val guessesDao: ValidGuessesDao) {
  private var guesses: Set<String>? = null
  private val guessesMutex = Mutex()

  private var answers: Set<String>? = null
  private val answersMutex = Mutex()

  suspend fun getAnswer(seed: Long): String = getAnswers().random(Random(seed))

  fun isValidGuess(guess: String): Boolean {
    val validGuesses = runBlocking { getGuesses() + getAnswers() }
    return validGuesses.contains(guess)
  }

  private suspend fun getAnswers(): Set<String> {
    answersMutex.withLock {
      if (answers == null) {
        answers = answersDao.getAll().map { it.value }.toSet()
      }
    }

    return answers!!
  }

  private suspend fun getGuesses(): Set<String> {
    guessesMutex.withLock {
      if (guesses == null) {
        guesses = guessesDao.getAll().map { it.value }.toSet()
      }
    }

    return guesses!!
  }
}
