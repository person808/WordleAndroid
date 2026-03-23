package com.kainalu.wordle.stats

import com.kainalu.wordle.settings.GameSettings
import com.kainalu.wordle.testHelpers.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class StatsViewModelTest {
  companion object {
    private val TEST_STATS = Stats()
  }

  @get:Rule val mainDispatcherRule = MainDispatcherRule()
  @get:Rule val mockkRule = MockKRule(this)

  @MockK lateinit var resultsRepository: ResultsRepository
  private lateinit var statsViewModel: StatsViewModel
  private val gameSettings = GameSettings(6)

  @Before
  fun setup() {
    coEvery { resultsRepository.getStats() } returns TEST_STATS
    statsViewModel = StatsViewModel(resultsRepository, gameSettings)
  }

  @Test
  fun `initial state`() = runTest {
    coEvery { resultsRepository.getStats() } coAnswers
      {
        delay(1000)
        TEST_STATS
      }
    statsViewModel = StatsViewModel(resultsRepository, gameSettings)

    assertTrue(statsViewModel.screenState.value is StatsScreenState.Loading)
  }

  @Test
  fun `loads stats when initialized`() = runTest {
    assertTrue(statsViewModel.screenState.value is StatsScreenState.Loaded)
  }

  @Test
  @Parameters(value = ["0,0,0", "1,1,100", "5,10,50"])
  fun `Win rate calculation accuracy`(wins: Int, gamesPlayed: Int, expectedWinRate: Double) =
    runTest {
      coEvery { resultsRepository.getStats() } returns
        Stats(games_played = gamesPlayed, wins = wins)
      statsViewModel = StatsViewModel(resultsRepository, gameSettings)
      mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

      assertEquals(
        expectedWinRate,
        (statsViewModel.screenState.value as StatsScreenState.Loaded).winRate,
        0.01,
      )
    }

  private fun gameDistributionValues() =
    arrayOf(
      // 1:1 mapping case
      arrayOf(
        mapOf(1 to 1, 2 to 2, 3 to 3, 4 to 4, 5 to 5, 6 to 6),
        mapOf(1 to 1, 2 to 2, 3 to 3, 4 to 4, 5 to 5, 6 to 6),
      ),
      // Check that omitted keys are mapped to zero
      arrayOf(mapOf(1 to 1, 3 to 3), mapOf(1 to 1, 2 to 0, 3 to 3, 4 to 0, 5 to 0, 6 to 0)),
      // Check that keys above maxGuesses are ignored
      arrayOf(mapOf(7 to 1), mapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0)),
    )

  @Test
  @Parameters(method = "gameDistributionValues")
  fun `game distribution mapping`(
    savedDistribution: Map<Int, Int>,
    expectedDistribution: Map<Int, Int>,
  ) = runTest {
    coEvery { resultsRepository.getStats() } returns
      Stats(wins = 1, guess_distribution = savedDistribution)
    statsViewModel = StatsViewModel(resultsRepository, gameSettings)
    mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

    assertEquals(
      expectedDistribution,
      (statsViewModel.screenState.value as StatsScreenState.Loaded).gameDistribution,
    )
  }

  @Test
  fun `simple stat mapping`() {
    // Check that stat values that are 1:1 mappings from storage to UI state are mapped correctly
    coEvery { resultsRepository.getStats() } returns
      Stats(games_played = 1, current_win_streak = 1, longest_win_streak = 1)
    statsViewModel = StatsViewModel(resultsRepository, gameSettings)
    mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()

    val state = statsViewModel.screenState.value as StatsScreenState.Loaded
    assertEquals(1, state.gamesPlayed)
    assertEquals(1, state.currentWinStreak)
    assertEquals(1, state.maxWinStreak)
  }
}
