package com.kainalu.wordle.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AllowedGuess::class, Answer::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun validAnswersDao(): ValidAnswersDao

  abstract fun validGuessesDao(): ValidGuessesDao
}
