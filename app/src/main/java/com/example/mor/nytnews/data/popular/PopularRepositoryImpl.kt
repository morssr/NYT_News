package com.example.mor.nytnews.data.popular

import co.touchlab.kermit.Logger
import com.example.mor.nytnews.data.popular.api.PopularPeriod
import com.example.mor.nytnews.data.popular.api.PopularService
import com.example.mor.nytnews.data.popular.cache.PopularDao
import com.example.mor.nytnews.data.popular.common.PopularModel
import com.example.mor.nytnews.data.popular.common.PopularType
import com.example.mor.nytnews.data.popular.common.toPopularEntityList
import com.example.mor.nytnews.data.popular.common.toPopularModelList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

private const val TAG = "PopularRepositoryImpl"

class PopularRepositoryImpl(
    private val api: PopularService,
    private val dao: PopularDao,
    logger: Logger
) : PopularRepository {

    private val log = logger.withTag(TAG)

    override fun getPopularsByTypeStream(
        type: PopularType,
        period: PopularPeriod,
        remoteSync: Boolean
    ): Flow<List<PopularModel>> = flow {
        log.d { "getPopularsByTypeStream called with type: $type, period: $period, remoteSync: $remoteSync" }

        //request populars articles from remote and insert to local db
        try {
            if (remoteSync) {
                val popularsResponse = api.getPopularArticles(type, period)

                if (!popularsResponse.isSuccessful) {
                    throw Exception("getPopularsByTypeStream failed with code: ${popularsResponse.code()}")
                }

                log.v { "getPopularsByTypeStream response is successful. results size: ${popularsResponse.body()?.results?.size}" }

                popularsResponse.body()?.results?.forEach {
                    log.v { "getPopularsByTypeStream result: $it" }
                }

                val popularsEntities = popularsResponse.body()?.toPopularEntityList(type)

                log.i { "$popularsEntities" }
                if (popularsEntities.isNullOrEmpty()) {
                    throw Exception("getPopularsByTypeStream failed with empty response")
                }
                log.v { "getPopularsByTypeStream mapping is successful. popularsEntities size: ${popularsEntities.size}" }

                //clear old popular articles for the same type
                dao.clearByType(type)
                //insert new popular articles
                dao.insertAll(popularsEntities)
            }
        } catch (e: Exception) {
            log.e { "getPopularsByTypeStream failed with exception: $e" }
        } finally {
            emitAll(dao.getPopularByTypeStream(type).map { it.toPopularModelList() })
        }

    }

    override fun getAllCachedPopularsStream(): Flow<List<PopularModel>> {
        return dao.getAllPopularStream().map { it.toPopularModelList() }
    }
}