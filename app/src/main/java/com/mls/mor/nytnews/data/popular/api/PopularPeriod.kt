package com.mls.mor.nytnews.data.popular.api

enum class PopularPeriod(private val period: String) {
    DAY("1"),
    WEEK("7"),
    MONTH("30");

    override fun toString() = period
}