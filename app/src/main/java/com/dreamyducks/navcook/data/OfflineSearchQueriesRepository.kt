package com.dreamyducks.navcook.data

import kotlinx.coroutines.flow.Flow

class OfflineSearchQueriesRepository(private val searchDao: SearchDao) : SearchQueriesRepository {
    override fun getAllSearchQueriesStream(): Flow<List<Query>> = searchDao.getAllQueries()
    override suspend fun insertQuery(query: Query) = searchDao.insert(query)
}