package com.kainalu.wordle.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kainalu.wordle.composables.GameDialog

@Composable
fun Stat(value: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), fontSize = 24.sp, textAlign = TextAlign.Center)
        Text(label, modifier = Modifier.width(28.dp), fontSize = 8.sp, textAlign = TextAlign.Center)
    }
}

@Preview
@Composable
fun StatPreview() {
    Stat(100, "Max Streak")
}

@Composable
fun StatsScreen(navController: NavController, viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.screenState.collectAsState()

    GameDialog(title = "Statistics", onDismissRequest = { navController.popBackStack() }) {
        when (val s = state) {
            is StatsScreenState.Loaded -> {
                val stats = mapOf(
                    "Games Played" to s.gamesPlayed,
                    "Win %" to s.winRate.toInt(),
                    "Current Streak" to s.currentWinStreak,
                    "Max Streak" to s.maxWinStreak,
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        ),
                    ) {
                        stats.forEach { (label, value) ->
                            Stat(value, label)
                        }
                    }

                    Text("Game Distribution", fontWeight = FontWeight.Bold)
                    GameDistributionChart(s.gameDistribution)
                }
            }
            else -> Unit
        }
    }
}