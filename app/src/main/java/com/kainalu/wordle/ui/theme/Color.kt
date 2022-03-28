package com.kainalu.wordle.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF4CAF50)

private val Green = Color(0xFF4CAF50)
private val Yellow = Color(0xFFFFD600)
private val Gray1 = Color(0xFFBDBDBD)
private val Gray2 = Color(0xFF616161)
private val Gray3 = Color(0xFFA4A4A4)
private val Gray4 = Color(0xFF424242)
private val Gray5 = Color(0xFFCFCFCF)

val Colors.correctGuess: Color
    get() = if (isLight) Green else Green

val Colors.partialMatch: Color
    get() = if (isLight) Yellow else Yellow

val Colors.incorrectGuess: Color
    get() = if (isLight) Gray2 else Gray4

val Colors.onSubmittedGuess: Color
    get() = Color.White

val Colors.emptySpaceBorder: Color
    get() = Gray1.copy(alpha = 0.5f)

val Colors.guessBorder: Color
    get() = if (isLight) Gray2 else Gray1.copy(alpha = 0.75f)

val Colors.keyboardButtonBackground: Color
    get() = if (isLight) Gray5 else Gray3