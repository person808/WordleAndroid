package com.kainalu.wordle.persistence

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ValidAnswersDao {
    @Query("SELECT * from validAnswers")
    suspend fun getAll(): List<Answer>
}