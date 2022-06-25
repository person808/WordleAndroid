package com.kainalu.wordle.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kainalu.wordle.ui.theme.incorrectGuess

private val LABEL_FONT_SIZE = 10.sp

@Preview
@Composable
fun ChartLinePreview() {
  ChartLine(text = "0")
}

@Composable
fun ChartLine(modifier: Modifier = Modifier, text: String) {
  Text(
      text,
      modifier =
          modifier
              .defaultMinSize(minWidth = 8.dp)
              .background(MaterialTheme.colors.incorrectGuess)
              .padding(horizontal = 8.dp, vertical = 2.dp),
      color = Color.White,
      fontSize = LABEL_FONT_SIZE,
      fontWeight = FontWeight.Bold,
  )
}

@Composable
fun GameDistributionChart(gameDistribution: Map<Int, Int>) {
  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    gameDistribution.forEach { (numGuesses, numGames) ->
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(numGuesses.toString(), fontSize = LABEL_FONT_SIZE)
        ChartLine(text = numGames.toString())
      }
    }
  }
}
