package com.kainalu.wordle.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.room.Room
import com.kainalu.wordle.persistence.AppDatabase
import com.kainalu.wordle.stats.Stats
import com.kainalu.wordle.stats.statsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

  @Provides
  fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, "wordle.db")
      .createFromAsset("wordle.db")
      .build()

  @Provides fun provideAnswersDao(db: AppDatabase) = db.validAnswersDao()

  @Provides fun provideGuessesDao(db: AppDatabase) = db.validGuessesDao()

  @Provides
  fun provideStatsDataStore(@ApplicationContext context: Context): DataStore<Stats> =
    context.statsDataStore
}
