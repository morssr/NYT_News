package com.mls.mor.nytnews.data.popular.common

enum class PopularType(private val type: String) {
    MOST_EMAILED("emailed"),
    MOST_SHARED("shared"),
    MOST_VIEWED("viewed");

    override fun toString() = type
}