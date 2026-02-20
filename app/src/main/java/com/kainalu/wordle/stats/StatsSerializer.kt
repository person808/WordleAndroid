package com.kainalu.wordle.stats

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import java.io.InputStream
import java.io.OutputStream

object StatsSerializer : Serializer<Stats> {
  override val defaultValue: Stats = Stats()

  override suspend fun readFrom(input: InputStream): Stats {
      return Stats.ADAPTER.decode(input)
  }

  override suspend fun writeTo(t: Stats, output: OutputStream) = Stats.ADAPTER.encode(stream = output, value = t)
}

val Context.statsDataStore: DataStore<Stats> by
  dataStore(fileName = "stats.pb", serializer = StatsSerializer)
