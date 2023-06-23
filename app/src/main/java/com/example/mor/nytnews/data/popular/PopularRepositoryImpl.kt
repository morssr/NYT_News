package com.example.mor.nytnews.data.popular

import co.touchlab.kermit.Logger
import com.example.mor.nytnews.data.popular.api.PopularPeriod
import com.example.mor.nytnews.data.popular.api.PopularService
import com.example.mor.nytnews.data.popular.cache.PopularDao
import com.example.mor.nytnews.data.popular.common.PopularModel
import com.example.mor.nytnews.data.popular.common.PopularType
import com.example.mor.nytnews.utilities.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "PopularRepositoryImpl"

class PopularRepositoryImpl(
    private val api: PopularService,
    private val dao: PopularDao,
    logger: Logger
) : PopularRepository {

    private val log = logger.withTag(TAG)

    override suspend fun getPopularsByTypeStream(
        type: PopularType,
        period: PopularPeriod,
        remoteSync: Boolean
    ): Response<Flow<List<PopularModel>>> {
        log.d { "getPopularsByTypeStream called with type: $type, period: $period, remoteSync: $remoteSync" }

        //request populars articles from remote and insert to local db
        if (remoteSync) {
            val popularsResponse = api.getPopularArticles(type, period)

            if (!popularsResponse.isSuccessful) {
                return Response.Failure(Exception(popularsResponse.message()))
            }

            val popularsEntities = popularsResponse.body()?.toPopularEntityList(type)

            if (popularsEntities.isNullOrEmpty()) {
                return Response.Failure(Exception("popularsEntities is null or empty"))
            }
            dao.insertAll(popularsEntities)
        }

        return Response.Success(
            dao.getPopularByTypeStream(type).map { it.toPopularModelList() })
    }

    override fun getAllCachedPopularsStream(): Flow<List<PopularModel>> {
        return dao.getAllPopularStream().map { it.toPopularModelList() }
    }
}