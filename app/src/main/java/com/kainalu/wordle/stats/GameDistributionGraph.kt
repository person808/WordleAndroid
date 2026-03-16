package com.kainalu.wordle.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ChartLinePreview() {
  MaterialTheme { ChartLine(text = "0") }
}

@Composable
fun ChartLine(modifier: Modifier = Modifier, text: String) {
  Text(
    text,
    modifier =
      modifier
        .defaultMinSize(minWidth = 8.dp)
        .background(MaterialTheme.colorScheme.secondary)
        .padding(horizontal = 8.dp, vertical = 2.dp),
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.onSecondary,
  )
}

@Composable
@Preview(showBackground = true)
fun GameDistributionChart(
  @PreviewParameter(GameDistributionParameterProvider::class) gameDistribution: Map<Int, Int>
) {
  val highestNumberOfGames = gameDistribution.values.max()

  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    gameDistribution.forEach { (numGuesses, numGames) ->
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text(numGuesses.toString(), style = MaterialTheme.typography.labelSmall)
        ChartLine(
          modifier =
            Modifier.then(
              if (numGames == 0) Modifier
              else Modifier.fillMaxWidth(numGames.toFloat() / highestNumberOfGames)
            ),
          text = numGames.toString(),
        )
      }
    }
  }
}

class GameDistributionParameterProvider : PreviewParameterProvider<Map<Int, Int>> {
  override val values = sequenceOf(mapOf(1 to 0, 2 to 4, 3 to 7, 4 to 3, 5 to 4, 6 to 2))
}
