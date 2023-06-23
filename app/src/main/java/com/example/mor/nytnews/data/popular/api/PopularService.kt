package com.example.mor.nytnews.data.popular.api

import com.example.mor.nytnews.data.API_KEY
import com.example.mor.nytnews.data.POPULAR_REQUEST_BASE_URL
import com.example.mor.nytnews.data.popular.PopularType
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

