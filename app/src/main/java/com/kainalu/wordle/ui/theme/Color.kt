package com.kainalu.wordle.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF4CAF50)

private val Green = Color(0xFF6AAA64)
private val DarkGreen = Color(0xFF538D4E)
private val Yellow = Color(0xFFC9B458)
private val DarkYellow = Color(0xFFB59F3B)
private val Gray1 = Color(0xFFBDBDBD)
private val Gray2 = Color(0xFF787C7E)
private val Gray3 = Color(0xFF818384)
private val Gray4 = Color(0xFF3A3A3C)
private val Gray5 = Color(0xFFD3D6DA)
private val Gray6 = Color(0xFF878A8C)
private val Gray7 = Color(0xFF565758)

val Colors.correctGuess: Color
    get() = if (isLight) Green else DarkGreen

val Colors.partialMatch: Color
    get() = if (isLight) Yellow else DarkYellow

val Colors.incorrectGuess: Color
    get() = if (isLight) Gray2 else Gray4

val Colors.submittedGuess: Color
    get() = Color.White

val Colors.emptySpaceBorder: Color
    get() = if (isLight) Gray1 else Gray4

val Colors.guessBorder: Color
    get() = if (isLight) Gray6 else Gray7

val Colors.keyboardButtonBackground: Color
    get() = if (isLight) Gray5 else Gray3