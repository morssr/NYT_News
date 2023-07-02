package com.example.mor.nytnews.data.popular

import com.example.mor.nytnews.data.popular.api.PopularPeriod
import com.example.mor.nytnews.data.popular.common.PopularModel
import com.example.mor.nytnews.data.popular.common.PopularType
import kotlinx.coroutines.flow.Flow

interface PopularRepository {

    fun getPopularsByTypeStream(
        type: PopularType,
        period: PopularPeriod,
        remoteSync: Boolean
    ): Flow<List<PopularModel>>

    fun getAllCachedPopularsStream(): Flow<List<PopularModel>>
}