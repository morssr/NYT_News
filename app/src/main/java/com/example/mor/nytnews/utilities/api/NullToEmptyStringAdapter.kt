package com.example.mor.nytnews.utilities.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader

private const val DEFAULT_NULL_STRING_PLACEHOLDER = ""

class NullToEmptyStringAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): String {
        if (reader.peek() != JsonReader.Token.NULL) {
            return reader.nextString()
        }
        reader.nextNull<Unit>()
        return DEFAULT_NULL_STRING_PLACEHOLDER
    }
}