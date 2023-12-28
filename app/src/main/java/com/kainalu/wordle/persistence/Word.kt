package com.kainalu.wordle.persistence

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["value"], unique = true)], tableName = "validAnswers")
data class Answer(@PrimaryKey(autoGenerate = true) val id: Int, val value: String)

@Entity(indices = [Index(value = ["value"], unique = true)], tableName = "validGuesses")
data class AllowedGuess(@PrimaryKey(autoGenerate = true) val id: Int, val value: String)
