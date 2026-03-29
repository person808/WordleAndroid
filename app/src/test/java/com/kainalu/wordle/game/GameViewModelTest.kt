package com.kainalu.wordle.game

import com.kainalu.wordle.game.words.WordsRepository
import com.kainalu.wordle.settings.GameSettings
import com.kainalu.wordle.stats.GameResult
import com.kainalu.wordle.stats.ResultsRepository
import com.kainalu.wordle.testHelpers.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class GameViewModelTest {
  companion object {
    private const val TEST_ANSWER = "hello"
  }

  @get:Rule val mockkRule = MockKRule(this)
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @MockK lateinit var wordsRepository: WordsRepository
  @RelaxedMockK lateinit var resultsRepository: ResultsRepository
  private val gameSettings = GameSettings(6)
  private val clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.of("UTC"))
  private lateinit var viewModel: GameViewModel

  @Before
  fun setup() {
    coEvery { wordsRepository.getAnswer(any()) } returns TEST_ANSWER
    coEvery { wordsRepository.isValidGuess(any()) } returns true

    viewModel = GameViewModel(resultsRepository, wordsRepository, gameSettings, clock)
  }

  @Test
  fun `initializes game state`() = runTest {
    val gameState = viewModel.gameState.value
    assertEquals(
      Active(settings = gameSettings, answer = "hello", date = LocalDate.now(clock)),
      gameState,
    )
  }

  @Test
  fun `guessLetter adds letter to last guess`() = runTest {
    "abcde".forEach { viewModel.guessLetter(it) }
    assertEquals(UnsubmittedGuess(5, "abcde"), (viewModel.gameState.value as Active).guesses.last())

    // Check that submission moves to next guess
    viewModel.submitAnswer()
    "fghij".forEach { viewModel.guessLetter(it) }
    assertEquals(UnsubmittedGuess(5, "fghij"), (viewModel.gameState.value as Active).guesses.last())
  }

  @Test
  fun `guessLetter does not add letters past max length`() = runTest {
    "abcdefg".forEach { viewModel.guessLetter(it) }
    assertEquals(UnsubmittedGuess(5, "abcde"), (viewModel.gameState.value as Active).guesses.last())
  }

  @Test
  fun `guessLetter does not add letters if game is finished`() = runTest {
    TEST_ANSWER.forEach { viewModel.guessLetter(it) }
    viewModel.submitAnswer()
    assertTrue(viewModel.gameState.value is Finished)

    viewModel.guessLetter('a')
    assertEquals(
      SubmittedGuess(TEST_ANSWER.map { GuessResult.Correct(it) }),
      (viewModel.gameState.value as Finished).guesses.last(),
    )
  }

  @Test
  fun `deleteLetter deletes last letter`() = runTest {
    val guess = "abcde"
    guess.forEach { viewModel.guessLetter(it) }

    for (i in 1..guess.length) {
      viewModel.deleteLetter()
      assertEquals(
        UnsubmittedGuess(5, guess.slice(0 until guess.length - i)),
        (viewModel.gameState.value as Active).guesses.last(),
      )
    }
  }

  @Test
  @Ignore("Collecting the event flow hangs even though the same pattern is used in the next test")
  fun `submitAnswer sends error event when guess is too short`() = runTest {
    val originalState = viewModel.gameState.value
    viewModel.submitAnswer()

    // TODO: Figure out why collecting the flow in this test hangs
    val event = viewModel.gameEvents.first()
    assertEquals(Event.GuessTooShort, event)
    // State should not change
    assertEquals(originalState, viewModel.gameState.value)
  }

  @Test
  fun `submitAnswer sends error event when guess is invalid`() = runTest {
    coEvery { wordsRepository.isValidGuess(any()) } returns false

    "abcde".forEach { viewModel.guessLetter(it) }
    val originalState = viewModel.gameState.value
    viewModel.submitAnswer()

    val event = viewModel.gameEvents.first()
    assertEquals(Event.GuessNotInWordList, event)
    // State should not change
    assertEquals(originalState, viewModel.gameState.value)
  }

  @Test
  fun `submitAnswer finishes game when guess is correct`() = runTest {
    TEST_ANSWER.forEach { viewModel.guessLetter(it) }
    launch { viewModel.submitAnswer() }.join()

    val event = viewModel.gameEvents.first()
    assertEquals(Event.GameFinished(answer = TEST_ANSWER, won = true), event)
    assertEquals(
      Finished(
        settings = gameSettings,
        answer = TEST_ANSWER,
        guesses = listOf(SubmittedGuess(TEST_ANSWER.map { GuessResult.Correct(it) })),
        guessResults =
          buildMap { TEST_ANSWER.forEach { char -> put(char, GuessResult.Correct(char)) } },
        date = LocalDate.now(clock),
      ),
      viewModel.gameState.value,
    )
    coVerify {
      resultsRepository.saveGameResult(
        GameResult(date = LocalDate.now(clock), numGuesses = 1, won = true)
      )
    }
  }

  @Test
  fun `submitAnswer finishes game when last answer is incorrect`() = runTest {
    val guess = "xxxxx"
    launch {
        repeat(gameSettings.maxGuesses) {
          guess.forEach { viewModel.guessLetter(it) }
          viewModel.submitAnswer()
        }
      }
      .join()

    val event = viewModel.gameEvents.first()
    assertEquals(Event.GameFinished(answer = TEST_ANSWER, won = false), event)
    assertEquals(
      Finished(
        settings = gameSettings,
        answer = TEST_ANSWER,
        guesses =
          List(gameSettings.maxGuesses) { SubmittedGuess(guess.map { GuessResult.Incorrect(it) }) },
        guessResults =
          buildMap { guess.forEach { char -> put(char, GuessResult.Incorrect(char)) } },
        date = LocalDate.now(clock),
      ),
      viewModel.gameState.value,
    )
    coVerify {
      resultsRepository.saveGameResult(
        GameResult(date = LocalDate.now(clock), numGuesses = gameSettings.maxGuesses, won = false)
      )
    }
  }

  private fun matchValues() =
    arrayOf(
      arrayOf("xxxxx", mapOf('x' to GuessResult.Incorrect('x'))),
      arrayOf(
        "hexxx",
        mapOf(
          'h' to GuessResult.Correct('h'),
          'e' to GuessResult.Correct('e'),
          'x' to GuessResult.Incorrect('x'),
        ),
      ),
      arrayOf(
        "hxxxe",
        mapOf(
          'h' to GuessResult.Correct('h'),
          'e' to GuessResult.PartialMatch('e'),
          'x' to GuessResult.Incorrect('x'),
        ),
      ),
      arrayOf(
        "hoxxo",
        mapOf(
          'h' to GuessResult.Correct('h'),
          'o' to GuessResult.Correct('o'),
          'x' to GuessResult.Incorrect('x'),
        ),
      ),
    )

  @Test
  @Parameters(method = "matchValues")
  fun `submitAnswer calculates matches correctly`(guess: String, expected: Map<Char, GuessResult>) =
    runTest {
      guess.forEach { viewModel.guessLetter(it) }
      viewModel.submitAnswer()

      assertEquals(expected, (viewModel.gameState.value as Active).guessResults)
    }

  @Test
  fun `submitAnswer updates matches correctly over multiple guesses`() = runTest {
    val guessSequence =
      listOf(
        "xxxxx" to mapOf('x' to GuessResult.Incorrect('x')),
        "hxxxe" to
          mapOf(
            'h' to GuessResult.Correct('h'),
            'e' to GuessResult.PartialMatch('e'),
            'x' to GuessResult.Incorrect('x'),
          ),
        "hexxe" to
          mapOf(
            'h' to GuessResult.Correct('h'),
            'e' to GuessResult.Correct('e'),
            'x' to GuessResult.Incorrect('x'),
          ),
      )

    guessSequence.forEach { (guess, expected) ->
      guess.forEach { viewModel.guessLetter(it) }
      viewModel.submitAnswer()
      assertEquals(expected, (viewModel.gameState.value as Active).guessResults)
    }
  }

  @Test
  fun `submitAnswer adds new UnsubmittedGuess after incorrect SubmittedGuess`() = runTest {
    val guess = "xxxxx"
    guess.forEach { viewModel.guessLetter(it) }
    viewModel.submitAnswer()

    assertEquals(
      listOf(
        SubmittedGuess(guess.map { GuessResult.Incorrect(it) }),
        UnsubmittedGuess(TEST_ANSWER.length),
      ),
      (viewModel.gameState.value as Active).guesses,
    )
  }

  @Test
  fun `submitAnswer does not add new UnsubmittedGuess if the guess cannot be submitted`() =
    runTest {
      val guess = "xxxxx"
      guess.forEach { viewModel.guessLetter(it) }
      viewModel.submitAnswer() // Should submit answer
      viewModel.submitAnswer() // Attempt to submit blank guess

      assertEquals(
        listOf(
          SubmittedGuess(guess.map { GuessResult.Incorrect(it) }),
          UnsubmittedGuess(TEST_ANSWER.length, ""),
        ),
        (viewModel.gameState.value as Active).guesses,
      )
    }
}
