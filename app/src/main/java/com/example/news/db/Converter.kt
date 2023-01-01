package com.example.news.db

import androidx.room.TypeConverter
import javax.xml.transform.Source

class Converter {

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}