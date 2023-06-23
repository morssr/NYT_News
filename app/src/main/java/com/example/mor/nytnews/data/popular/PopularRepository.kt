package com.example.mor.nytnews.data.popular

import com.example.mor.nytnews.data.popular.api.PopularPeriod
import com.example.mor.nytnews.data.popular.common.PopularModel
import com.example.mor.nytnews.data.popular.common.PopularType
import com.example.mor.nytnews.utilities.Response
import kotlinx.coroutines.flow.Flow

interface PopularRepository {

    suspend fun getPopularsByTypeStream(
        type: PopularType,
        period: PopularPeriod,
        remoteSync: Boolean
    ): Response<Flow<List<PopularModel>>>

    fun getAllCachedPopularsStream(): Flow<List<PopularModel>>
}