package com.sample.newsapp.data.db

import androidx.room.TypeConverter
import com.sample.newsapp.data.db.models.Source

class Converters {
    // Source OBJECT
    @TypeConverter
    fun fromSource(source: Source): String = source.name

    @TypeConverter
    fun toSource(name: String): Source = Source(name, name)
}