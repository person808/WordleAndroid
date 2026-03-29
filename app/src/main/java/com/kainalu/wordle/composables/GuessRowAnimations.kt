package com.kainalu.wordle.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.kainalu.wordle.settings.WORD_SIZE
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface FlipAnimationValues {
  /** State that determines whether to show the revealed letter result */
  val showResult: State<Boolean>
  /** Modifier to attach to the composable to animate */
  val modifier: Modifier
}

private data class FlipAnimationValuesImpl(
  val rotationX: Animatable<Float, *>,
  val _showResult: MutableState<Boolean>,
) : FlipAnimationValues {
  override val showResult: State<Boolean>
    get() = _showResult

  override val modifier =
    Modifier.graphicsLayer { rotationX = this@FlipAnimationValuesImpl.rotationX.value }
}

class FlipAnimation {
  private val _values =
    List(WORD_SIZE) { FlipAnimationValuesImpl(Animatable(0f), mutableStateOf(false)) }
  val values: List<FlipAnimationValues>
    get() = _values

  suspend fun play() {
    val halfFlipDuration = 100
    _values.forEach { value ->
      value.rotationX.animateTo(
        90f,
        tween(durationMillis = halfFlipDuration, easing = LinearEasing),
      )
      value._showResult.value = true
      value.rotationX.animateTo(-90f, snap())
      value.rotationX.animateTo(0f, tween(durationMillis = halfFlipDuration, easing = LinearEasing))
    }
  }
}

interface WinnerAnimationValues {
  /** Modifier to attach to the composable to animate */
  val modifier: Modifier
}

private data class WinnerAnimationValuesImpl(val translationY: Animatable<Float, *>) :
  WinnerAnimationValues {
  override val modifier =
    Modifier.graphicsLayer { translationY = this@WinnerAnimationValuesImpl.translationY.value }
}

class WinnerAnimation {
  private val _values: List<WinnerAnimationValuesImpl> =
    List(WORD_SIZE) { WinnerAnimationValuesImpl(Animatable(0f)) }
  val values: List<WinnerAnimationValues>
    get() = _values

  suspend fun play() = coroutineScope {
    _values.zip(ANIMATIONS).forEach { (animationValues, animation) ->
      launch { animation(animationValues.translationY) }
      delay(30.milliseconds)
    }
  }

  companion object {
    private val ANIMATIONS =
      arrayOf<suspend (Animatable<Float, *>) -> Unit>(
        { animatable ->
          floatArrayOf(-50f, 0f).forEach { animatable.animateTo(it, tween(durationMillis = 100)) }
        },
        { animatable ->
          floatArrayOf(-50f, 0f, -35f, 0f).forEach {
            animatable.animateTo(it, tween(durationMillis = 100))
          }
        },
        { animatable ->
          floatArrayOf(-50f, 0f, -15f, 0f).forEach {
            animatable.animateTo(it, tween(durationMillis = 100))
          }
        },
        { animatable ->
          floatArrayOf(-50f, 0f).forEach { animatable.animateTo(it, tween(durationMillis = 100)) }
        },
        { animatable ->
          floatArrayOf(-45f, 0f, -15f, 0f).forEach {
            animatable.animateTo(it, tween(durationMillis = 100))
          }
        },
      )

    init {
      check(ANIMATIONS.size == WORD_SIZE)
    }
  }
}
