package com.kainalu.wordle.stats

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResultsRepositoryTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val testDataStore = context.statsDataStore
  private val repository = ResultsRepository(testDataStore)

  @After
  fun cleanup() {
    // Restore datastore to default state
    runBlocking { testDataStore.updateData { StatsSerializer.defaultValue } }
  }

  @Test
  fun returnsDefaultStatsWhenNoStatsStored() = runTest {
    val storedStats = Stats()
    val stats = repository.getStats()
    assertEquals(storedStats, stats)
  }

  @Test
  fun skipsStatsUpdateIfGameHasBeenPlayedToday() = runTest {
    val currentDate = LocalDate.now()
    repository.saveGameResult(GameResult(currentDate, 3, true))
    repository.saveGameResult(GameResult(currentDate, 6, false))
    val stats = repository.getStats()
    assertEquals(
      Stats(
        wins = 1,
        games_played = 1,
        last_completed_game_date =
          Date(
            year = currentDate.year,
            month = currentDate.monthValue,
            day = currentDate.dayOfMonth,
          ),
        longest_win_streak = 1,
        current_win_streak = 1,
        guess_distribution = mapOf(3 to 1),
      ),
      stats,
    )
  }

  @Test
  fun updatesGamesPlayed() = runTest {
    val currentDate = LocalDate.now()
    repository.saveGameResult(GameResult(currentDate, 3, true))
    repository.saveGameResult(GameResult(currentDate.plusDays(1), 6, false))
    assertEquals(2, repository.getStats().games_played)
  }

  @Test
  fun updatesWins() = runTest {
    val currentDate = LocalDate.now()
    repository.saveGameResult(GameResult(currentDate, 3, true))
    repository.saveGameResult(GameResult(currentDate.plusDays(1), 6, false))
    repository.saveGameResult(GameResult(currentDate.plusDays(2), 6, true))
    assertEquals(2, repository.getStats().wins)
  }

  @Test
  fun updatesGuessDistribution() = runTest {
    val currentDate = LocalDate.now()
    repository.saveGameResult(GameResult(currentDate, 3, true))
    // Losses should be excludeed
    repository.saveGameResult(GameResult(currentDate.plusDays(1), 6, false))
    repository.saveGameResult(GameResult(currentDate.plusDays(2), 3, true))
    repository.saveGameResult(GameResult(currentDate.plusDays(3), 1, true))
    assertEquals(mapOf(1 to 1, 3 to 2), repository.getStats().guess_distribution)
  }

  @Test
  fun updatesWinStreak() = runTest {
    val currentDate = LocalDate.now()
    repository.saveGameResult(GameResult(currentDate, 3, true))
    assertEquals(1, repository.getStats().current_win_streak)
    assertEquals(1, repository.getStats().longest_win_streak)

    repository.saveGameResult(GameResult(currentDate.plusDays(1), 3, false))
    assertEquals(0, repository.getStats().current_win_streak)
    assertEquals(1, repository.getStats().longest_win_streak)

    repository.saveGameResult(GameResult(currentDate.plusDays(2), 3, true))
    assertEquals(1, repository.getStats().current_win_streak)
    assertEquals(1, repository.getStats().longest_win_streak)

    repository.saveGameResult(GameResult(currentDate.plusDays(3), 3, true))
    assertEquals(2, repository.getStats().current_win_streak)
    assertEquals(2, repository.getStats().longest_win_streak)
  }
}
