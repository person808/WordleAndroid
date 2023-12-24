package com.kainalu.wordle.stats

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ResultsRepository @Inject constructor(private val statsDataStore: DataStore<Stats>) {

  suspend fun saveGameResult(result: GameResult) {
    statsDataStore.updateData { stats ->
      // Skip update if we've already save the result for the current day
      if (
        stats.lastCompletedGameDate != Date.getDefaultInstance() &&
          stats.lastCompletedGameDate.toLocalDate() == LocalDate.now()
      ) {
        return@updateData stats
      }

      val newWinStreak = calculateWinStreak(stats, result)
      stats
        .toBuilder()
        .apply {
          wins = if (result.won) wins + 1 else wins
          lastCompletedGameDate =
            Date.newBuilder()
              .apply {
                year = result.date.year
                month = result.date.monthValue
                day = result.date.dayOfMonth
              }
              .build()
          currentWinStreak = newWinStreak
          longestWinStreak = if (newWinStreak > longestWinStreak) newWinStreak else longestWinStreak
          gamesPlayed += 1

          if (result.won) {
            putGuessDistribution(
              result.numGuesses,
              guessDistributionMap.getOrDefault(result.numGuesses, 0) + 1
            )
          }
        }
        .build()
    }
  }

  suspend fun getStats(): Stats {
    return statsDataStore.data.first()
  }

  private fun calculateWinStreak(stats: Stats, result: GameResult): Int {
    val playedPreviousDay =
      stats.lastCompletedGameDate != Date.getDefaultInstance() &&
        ChronoUnit.DAYS.between(stats.lastCompletedGameDate.toLocalDate(), result.date) > 1L
    return when {
      // Check if we have a winstreak to extend from the previous day
      result.won && playedPreviousDay -> {
        stats.currentWinStreak + 1
      }
      // No previously played game so the player has won their first played game!
      result.won -> 1
      else -> 0
    }
  }
}

fun Date.toLocalDate(): LocalDate = LocalDate.of(year, month, day)
