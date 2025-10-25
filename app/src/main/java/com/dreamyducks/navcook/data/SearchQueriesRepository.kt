package com.dreamyducks.navcook.data

import kotlinx.coroutines.flow.Flow

interface SearchQueriesRepository {
    fun getAllSearchQueriesStream(): Flow<List<Query>>

    suspend fun insertQuery(query: Query)
}