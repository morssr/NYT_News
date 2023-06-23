package com.example.mor.nytnews.data.popular

enum class PopularType(private val type: String) {
    MOST_EMAILED("emailed"),
    MOST_SHARED("shared"),
    MOST_VIEWED("viewed");

    override fun toString() = type
}