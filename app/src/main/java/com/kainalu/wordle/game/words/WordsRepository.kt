package com.kainalu.wordle.game.words

import com.kainalu.wordle.persistence.ValidAnswersDao
import com.kainalu.wordle.persistence.ValidGuessesDao
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.random.Random

class WordsRepository @Inject constructor(
    private val answersDao: ValidAnswersDao,
    private val guessesDao: ValidGuessesDao
) {
    suspend fun getAnswer(): String {
        val seed = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        return answersDao.getAll().random(Random(seed)).value
    }

    suspend fun getValidGuessesSet(): Set<String> =
        guessesDao.getAll().map { it.value }.toSet() + answersDao.getAll().map { it.value }
}