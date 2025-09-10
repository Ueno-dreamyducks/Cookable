package com.dreamyducks.navcook.network

import retrofit2.http.POST
import retrofit2.http.QueryMap

interface NavCookApiService {
    @POST("macros/s/AKfycbz0Dg9j3ExhW6lOhGPD4-CAqeKX7P4k0C7BQbD6cMHvsAJLpkKkjUHAkg0DxaV-4ShW/exec")
    suspend fun onSearch(@QueryMap params: Map<String, String>) : List<SearchResult>
}