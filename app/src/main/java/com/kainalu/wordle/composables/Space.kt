package com.kainalu.wordle.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kainalu.wordle.ui.theme.*

private val SPACE_SIZE = 48.dp
private val BORDER_WIDTH = 2.dp

@Composable
private fun SpaceBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.size(SPACE_SIZE), contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
private fun SpaceLetter(letter: Char, color: Color) {
    Text(
        text = letter.uppercase(),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Preview(showBackground = true)
@Composable
fun EmptySpace() {
    SpaceBox(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .border(BORDER_WIDTH, MaterialTheme.colors.emptySpaceBorder)
    ) {}
}

@Preview(showBackground = true)
@Composable
fun IncorrectSpace(@PreviewParameter(SampleLetterProvider::class) letter: Char) {
    SpaceBox(modifier = Modifier.background(MaterialTheme.colors.incorrectGuess)) {
        SpaceLetter(letter = letter, color = MaterialTheme.colors.submittedGuess)
    }
}

@Preview(showBackground = true)
@Composable
fun PartialMatchSpace(@PreviewParameter(SampleLetterProvider::class) letter: Char) {
    SpaceBox(modifier = Modifier.background(MaterialTheme.colors.partialMatch)) {
        SpaceLetter(letter = letter, color = MaterialTheme.colors.submittedGuess)
    }
}

@Preview(showBackground = true)
@Composable
fun CorrectSpace(@PreviewParameter(SampleLetterProvider::class) letter: Char) {
    SpaceBox(modifier = Modifier.background(MaterialTheme.colors.correctGuess)) {
        SpaceLetter(letter = letter, color = MaterialTheme.colors.submittedGuess)
    }
}

@Preview(showBackground = true)
@Composable
fun GuessSpace(@PreviewParameter(SampleLetterProvider::class) letter: Char) {
    SpaceBox(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .border(BORDER_WIDTH, MaterialTheme.colors.guessBorder)
    ) {
        SpaceLetter(letter = letter, color = MaterialTheme.colors.onSurface)
    }
}

class SampleLetterProvider : PreviewParameterProvider<Char> {
    override val values = sequenceOf('a')
}