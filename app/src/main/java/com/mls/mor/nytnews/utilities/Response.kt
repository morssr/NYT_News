package com.mls.mor.nytnews.utilities

sealed class Response<out T> {

    data class Success<out T>(
        val data: T
    ): Response<T>()

    data class Failure<out T>(
        val error: Exception
    ): Response<T>()
}