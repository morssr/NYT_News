package com.example.mor.nytnews.data.utils.db.coverters

import androidx.annotation.Keep
import androidx.room.TypeConverter
import com.example.mor.nytnews.data.popular.PopularType

@Keep
class PopularTypeConverter {

    @TypeConverter
    fun stringToPopularType(value: String?): PopularType? {
        return if (value == null) null else PopularType.valueOf(value)
    }

    @TypeConverter
    fun fromPopularTypeToString(value: PopularType?): String? {
        return value?.name
    }
}