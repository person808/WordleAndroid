package com.kainalu.wordle.di

import com.kainalu.wordle.settings.GameSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

private const val MAX_GUESSES = 6

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    fun providesGameSettings() = GameSettings(maxGuesses = MAX_GUESSES)
}