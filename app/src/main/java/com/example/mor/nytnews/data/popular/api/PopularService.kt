package com.example.mor.nytnews.data.popular.api

import com.example.mor.nytnews.data.API_KEY
import com.example.mor.nytnews.data.POPULAR_REQUEST_BASE_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface PopularService {

    @GET("$POPULAR_REQUEST_BASE_URL/{type}/{period}.json?api-key=$API_KEY")
    suspend fun getPopularArticles(
        @Path("type") type: PopularType = PopularType.MOST_VIEWED,
        @Path("period") period: PopularPeriod = PopularPeriod.DAY
    ): Response<PopularResponse>
}

enum class PopularType(private val type: String) {
    MOST_EMAILED("emailed"),
    MOST_SHARED("shared"),
    MOST_VIEWED("viewed");

    override fun toString() = type
}

enum class PopularPeriod(private val period: String) {
    DAY("1"),
    WEEK("7"),
    MONTH("30");

    override fun toString() = period
}