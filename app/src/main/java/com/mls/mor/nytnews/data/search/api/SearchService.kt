package com.mls.mor.nytnews.data.search.api

import com.mls.mor.nytnews.data.API_KEY
import com.mls.mor.nytnews.data.SEARCH_REQUEST_BASE_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET(SEARCH_REQUEST_BASE_URL)
    suspend fun getSearchResults(
        @Query("q")
        query: String,
        @Query("page")
        page: Int = 0,
        @Query("api-key")
        key: String = API_KEY
    ): Response<SearchResponse>
}