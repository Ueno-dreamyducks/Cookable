package com.dreamyducks.navcook.data

import com.dreamyducks.navcook.network.NavCookApiService
import com.dreamyducks.navcook.network.Recipe
import com.dreamyducks.navcook.network.SearchResult

interface SearchRepository {
    suspend fun onSearch(params: MutableMap<String, String>): List<SearchResult>
    suspend fun getRecipe(params: MutableMap<String, String>) : Recipe
    suspend fun  getTodaysRecipe(params: MutableMap<String, String>) : Recipe
}

class NetworkSearchRepository(
    private val navCookApiService: NavCookApiService
) : SearchRepository {
    override suspend fun onSearch(params: MutableMap<String, String>): List<SearchResult> =
        navCookApiService.onSearch(params)

    override suspend fun getRecipe(params: MutableMap<String, String>): Recipe =
        navCookApiService.getRecipe(params)

    override suspend fun getTodaysRecipe(params: MutableMap<String, String>): Recipe =
        navCookApiService.getTodaysId(params)
}