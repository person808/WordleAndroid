package com.kainalu.wordle.persistence

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ValidGuessesDao {
  @Query("SELECT * FROM validGuesses") suspend fun getAll(): List<AllowedGuess>
}
