package com.kainalu.wordle.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kainalu.wordle.settings.GameSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel
@Inject
constructor(
  private val resultsRepository: ResultsRepository,
  private val gameSettings: GameSettings,
) : ViewModel() {

  private val _screenState = MutableStateFlow<StatsScreenState>(StatsScreenState.Loading)
  val screenState: StateFlow<StatsScreenState> = _screenState

  init {
    viewModelScope.launch {
      val stats = resultsRepository.getStats()
      _screenState.value =
        StatsScreenState.Loaded(
          gamesPlayed = stats.gamesPlayed,
          winRate = stats.wins.toDouble() / stats.gamesPlayed * 100,
          currentWinStreak = stats.currentWinStreak,
          maxWinStreak = stats.longestWinStreak,
          gameDistribution =
            buildMap {
              (1..gameSettings.maxGuesses).forEach { guessNum ->
                set(guessNum, stats.guessDistributionMap.getOrDefault(guessNum, 0))
              }
            }
        )
    }
  }
}
