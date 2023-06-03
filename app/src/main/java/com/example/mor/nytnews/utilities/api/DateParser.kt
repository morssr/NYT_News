package com.example.mor.nytnews.utilities.api

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "DateParser"

fun parseDateFromString(dateString: String): Date {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val date = format.parse(dateString)

        if (date == null) {
            Log.w(TAG, "parseDate: parsed date is null, return current date")
            return Date()
        }
        return date
    } catch (e: Exception) {
        Log.e(TAG, "parseDate: parse date is failed, current date is returned", e)
        Date()
    }
}

fun parseDateToShortString(date: Date): String {
    return try {
        val format = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        format.format(date)
    } catch (e: Exception) {
        Log.e(TAG, "parseDate: parse date is failed, current date is returned", e)
        ""
    }
}