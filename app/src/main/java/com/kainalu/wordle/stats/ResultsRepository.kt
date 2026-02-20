package com.kainalu.wordle.stats

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class ResultsRepository @Inject constructor(private val statsDataStore: DataStore<Stats>) {

  suspend fun saveGameResult(result: GameResult) {
    statsDataStore.updateData { stats ->
      // Skip update if we've already saved the result for the current day
      if (
        stats.last_completed_game_date != Date() &&
          stats.last_completed_game_date?.toLocalDate() == LocalDate.now()
      ) {
        return@updateData stats
      }

      val newWinStreak = calculateWinStreak(stats, result)
      stats.copy(
        wins = if (result.won) stats.wins + 1 else stats.wins,
        last_completed_game_date =
          Date(year = result.date.year, month = result.date.monthValue, day = result.date.dayOfMonth),
        current_win_streak = newWinStreak,
        longest_win_streak = if (newWinStreak > stats.longest_win_streak) newWinStreak else stats.longest_win_streak,
        games_played = stats.games_played + 1,
        guess_distribution = stats.guess_distribution.toMutableMap().apply {
          put(result.numGuesses, getOrDefault(result.numGuesses, 0) + 1)
        }
      )
    }
  }

  suspend fun getStats(): Stats {
    return statsDataStore.data.first()
  }

  private fun calculateWinStreak(stats: Stats, result: GameResult): Int {
    val playedPreviousDay =
      stats.last_completed_game_date != null && stats.last_completed_game_date != Date() &&
        ChronoUnit.DAYS.between(stats.last_completed_game_date.toLocalDate(), result.date) > 1L
    return when {
      // Check if we have a winstreak to extend from the previous day
      result.won && playedPreviousDay -> {
        stats.current_win_streak + 1
      }
      // No previously played game so the player has won their first played game!
      result.won -> 1
      else -> 0
    }
  }
}

fun Date.toLocalDate(): LocalDate = LocalDate.of(year, month, day)
