package com.kainalu.wordle.stats

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object StatsSerializer : Serializer<Stats> {
    override val defaultValue: Stats = Stats.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Stats {
        try {
            return Stats.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Stats, output: OutputStream) = t.writeTo(output)
}

val Context.statsDataStore: DataStore<Stats> by dataStore(
    fileName = "stats.pb",
    serializer = StatsSerializer
)
