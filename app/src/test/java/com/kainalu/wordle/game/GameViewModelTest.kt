package com.kainalu.wordle.game

import com.kainalu.wordle.game.words.WordsRepository
import com.kainalu.wordle.settings.GameSettings
import com.kainalu.wordle.stats.ResultsRepository
import com.kainalu.wordle.testHelpers.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameViewModelTest {
  @get:Rule val mockkRule = MockKRule(this)
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @MockK lateinit var wordsRepository: WordsRepository
  @MockK(relaxUnitFun = true) lateinit var resultsRepository: ResultsRepository
  private val clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.of("UTC"))
  private lateinit var viewModel: GameViewModel

  @Before
  fun setup() {
    coEvery { wordsRepository.getAnswer(any()) } returns "HELLO"
    coEvery { wordsRepository.isValidGuess(any()) } returns true

    viewModel = GameViewModel(resultsRepository, wordsRepository, GameSettings(6), clock)
  }

  @Test
  fun `initializes game state`() = runTest {
    val gameState = viewModel.gameState.value
    assertEquals(
      GameState.Active(answer = "HELLO", maxGuesses = 6, date = LocalDate.now(clock)),
      gameState,
    )
  }
}
