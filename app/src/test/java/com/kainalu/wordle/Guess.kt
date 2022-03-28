package com.kainalu.wordle

import com.kainalu.wordle.game.UnsubmittedGuess
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GuessTests {
    @Test
    fun `UnsubmittedGuess insert inserts character`() {
        assertEquals(UnsubmittedGuess(WORD_SIZE, "a"), UnsubmittedGuess(WORD_SIZE).insert('a'))
    }

    @Test
    fun `UnsubmittedGuess insert when full is no-op`() {
        assertEquals(
            UnsubmittedGuess(WORD_SIZE, "abcde"),
            UnsubmittedGuess(WORD_SIZE, "abcde").insert('f')
        )
    }

    @Test
    fun `UnsubmittedGuess delete deletes last character`() {
        assertEquals(UnsubmittedGuess(WORD_SIZE, "ab"), UnsubmittedGuess(WORD_SIZE, "abc").delete())
    }

    @Test
    fun `UnsubmittedGuess delete is no-op when empty`() {
        assertEquals(UnsubmittedGuess(WORD_SIZE), UnsubmittedGuess(WORD_SIZE).delete())
    }

    companion object {
        private const val WORD_SIZE = 5
    }
}