package com.mls.mor.nytnews.utilities

sealed class ApiResponse<out T> {
    data class Success<out T>(
        val data: T
    ) : ApiResponse<T>()

    data class Failure<out T>(
        val fallbackData: T? = null,
        val error: Exception
    ) : ApiResponse<T>()
}
