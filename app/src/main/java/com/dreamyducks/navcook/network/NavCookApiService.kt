package com.dreamyducks.navcook.network

import retrofit2.http.POST
import retrofit2.http.QueryMap

interface NavCookApiService {
    @POST("macros/s/AKfycbwgmIfTlvHTHoMkzILVvhZv6BdQDpi_Ko9qbp1-QjAucgqDXiof-HNgJTlXP6Vfg8fP/exec")
    suspend fun onSearch(@QueryMap params: Map<String, String>) : List<SearchResult>

    @POST("macros/s/AKfycbwgmIfTlvHTHoMkzILVvhZv6BdQDpi_Ko9qbp1-QjAucgqDXiof-HNgJTlXP6Vfg8fP/exec")
    suspend fun getRecipe(@QueryMap params: Map<String, String>) : Recipe
}