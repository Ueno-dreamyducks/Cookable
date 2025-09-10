package com.dreamyducks.navcook.data

import com.dreamyducks.navcook.network.NavCookApiService
import com.dreamyducks.navcook.network.SearchResult

interface SearchRepository {
    suspend fun onSearch(params: MutableMap<String, String>): List<SearchResult>
}

class NetworkSearchRepository(
    private val navCookApiService: NavCookApiService
) : SearchRepository {
    override suspend fun onSearch(params: MutableMap<String, String>): List<SearchResult> =
        navCookApiService.onSearch(params)
}