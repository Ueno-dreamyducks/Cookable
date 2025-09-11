package com.dreamyducks.navcook.network

import retrofit2.http.POST
import retrofit2.http.QueryMap

interface NavCookApiService {
    @POST("macros/s/AKfycbxShHwJe8n3edoJ64RNh0VmKGA_8sRAmsR-J9ebDMmYDepHn3mUrj90oC0CagkoDqbE/exec")
    suspend fun onSearch(@QueryMap params: Map<String, String>) : List<SearchResult>

    @POST("macros/s/AKfycbxShHwJe8n3edoJ64RNh0VmKGA_8sRAmsR-J9ebDMmYDepHn3mUrj90oC0CagkoDqbE/exec")
    suspend fun getRecipe(@QueryMap params: Map<String, String>) : Recipe
}