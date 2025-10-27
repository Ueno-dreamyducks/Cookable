package com.dreamyducks.navcook.data.database.searchQueries

import kotlinx.coroutines.flow.Flow

interface SearchQueriesRepository {
    fun getAllSearchQueriesStream(): Flow<List<Query>>

    suspend fun insertQuery(query: Query)
}