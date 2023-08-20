package com.mls.mor.nytnews.data.popular.api

import com.mls.mor.nytnews.data.API_KEY
import com.mls.mor.nytnews.data.POPULAR_REQUEST_BASE_URL
import com.mls.mor.nytnews.data.popular.common.PopularType
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

