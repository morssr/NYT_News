package com.example.mor.nytnews.data.utils.db.coverters

import androidx.annotation.Keep
import androidx.room.TypeConverter
import java.util.Date

@Keep
class DateTypeConverter {
    @TypeConverter
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return value?.time
    }
}