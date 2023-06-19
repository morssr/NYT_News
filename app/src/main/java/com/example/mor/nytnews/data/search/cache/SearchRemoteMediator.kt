package com.example.mor.nytnews.data.search.cache

import android.content.res.Resources.NotFoundException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.data.AppDatabase
import com.example.mor.nytnews.data.search.api.SearchService
import com.example.mor.nytnews.data.search.toSearchEntity
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "SearchRemoteMediator"
private const val STARTING_PAGE_INDEX = 0

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val appDatabase: AppDatabase,
    private val searchService: SearchService,
    logger: Logger
) : RemoteMediator<Int, SearchEntity>() {

    private val log = logger.withTag(TAG)

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchEntity>
    ): MediatorResult {
        return try {
            log.i { "START LOAD FUNCTION" }
            log.d { "load: type: $loadType || paging state anchor pos: ${state.anchorPosition} | page size: ${state.pages.size} | ${Thread.currentThread().name}" }
            // find the required page
            val page = when (loadType) {
                // if loadType is refresh, it means that is the first time we are loading data for this query and the initial page is returned
                LoadType.REFRESH -> {
                    log.v { "load: LoadType.REFRESH" }
                    STARTING_PAGE_INDEX
                }

                // if loadType is prepend, it means that the user is scrolling up and we need to load the previous page
                LoadType.PREPEND -> {
                    log.v { "load: LoadType.PREPEND. return end of pagination true" }
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                // if loadType is append, it means that the user is scrolling down and we need to load the next page
                LoadType.APPEND -> {

                    // get the last item from the cached list
                    val lastRemoteKey = getRemoteKeyForLastItem(state)

                    if (lastRemoteKey == null) {
                        log.v { "load: LoadType.APPEND. last item is null. return end of pagination true" }
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    log.v { "load: LoadType.APPEND. last item is not null. return next key: ${lastRemoteKey.nextKey}" }
                    lastRemoteKey.nextKey
                }
            }

            log.d { "load: service request next page: $page with query $query" }

            val apiResponse = searchService.getSearchResults(query, page ?: STARTING_PAGE_INDEX)

            if (!apiResponse.isSuccessful) {
                log.e { "load: apiResponse is not successful response code: ${apiResponse.code()}. return error" }
                return MediatorResult.Error(HttpException(apiResponse))
            }

            val searchResults = apiResponse.body()?.response?.docs

            // if its initial load and search results is null or empty, return error
            if (loadType == LoadType.REFRESH && searchResults.isNullOrEmpty()) {
                log.e { "load: apiResponse is successful but search results is null or empty. return error" }
                return MediatorResult.Error(NotFoundException("Search results not found"))
            }

            //if the returning list is empty, it means we reached the end of pagination for this query
            val endOfPaginationReached = searchResults.isNullOrEmpty()
            //if page is equal to starting index, it means there is no previous page else page - 1
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page?.minus(1)
            //if endOfPaginationReached is true, it means there is no next page else page + 1
            val nextKey = if (endOfPaginationReached) null else page?.plus(1)
            log.v { "load: prevKey: $prevKey | nextKey: $nextKey" }

            appDatabase.withTransaction {
                // clear the tables on refresh
                if (loadType == LoadType.REFRESH) {
                    appDatabase.searchRemoteKeysDao().clearRemoteKeys()
                    appDatabase.searchDao().clearSearchResults()
                    log.v { "load: LoadType.REFRESH. clear remote keys and search results" }
                }

                searchResults?.let {
                    log.v { "search result is not null" }
                    val keys = searchResults.map {
                        SearchRemoteKeysEntity(
                            id = it._id,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    }
                    appDatabase.searchRemoteKeysDao().insertAll(keys)
                    appDatabase.searchDao().insertAll(searchResults.map { it.toSearchEntity() })
                    log.v { "load:transaction insert remote keys and search results" }
                }
            }

            log.v { "load: endOfPaginationReached: $endOfPaginationReached" }
            log.i { "END LOAD FUNCTION" }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            log.e(e) { "load: IOException" }
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            log.e(e) { "load: HttpException" }
            MediatorResult.Error(e)
        }
    }

    /**
     * get the remote key for the last item retrieved
     */
    //TODO FIX last item is null
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SearchEntity>): SearchRemoteKeysEntity? {
        log.v { "getRemoteKeyForLastItem: paging state anchor pos: ${state.anchorPosition} | page size: ${state.pages.size}" }
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull()?.lastOrNull()
            ?.let {
                // Get the remote keys of the last item retrieved
                val keys = appDatabase.searchRemoteKeysDao().remoteKeysSearchId(it.id)
                log.v { "getRemoteKeyForLastItem: the last key from the last page. entity id: ${it.id} | keys: $keys" }
                keys
            }
    }
//    /**
//     * get the remote key for the last item retrieved
//     */
//    //TODO check if this is the correct way to get the last item,
//    // if the bug of the last item being null is returned consider using "state.pages.last().last()"
//    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, SearchEntity>): SearchRemoteKeysEntity? {
//        log.v { "getRemoteKeyForLastItem: paging state anchor pos: ${state.anchorPosition} | page size: ${state.pages.size}" }
//        // Get the last page that was retrieved, that contained items.
//        // From that last page, get the last item
//        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
//            ?.let {
//                // Get the remote keys of the last item retrieved
//                val keys = appDatabase.searchRemoteKeysDao().remoteKeysSearchId(it.id)
//                log.v { "getRemoteKeyForLastItem: the last key from the last page. entity id: ${it.id} | keys: $keys" }
//                keys
//            }
//    }
}