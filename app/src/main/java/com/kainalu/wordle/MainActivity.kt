package com.kainalu.wordle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.kainalu.wordle.ui.theme.WordleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Turn off the decor fitting system windows, which means we need to fit it with insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WordleTheme {
                App()
            }
        }
    }
}