package com.dreamyducks.navcook.network

import retrofit2.http.POST
import retrofit2.http.QueryMap

interface NavCookApiService {
    @POST("macros/s/AKfycbzpMt2j2JdcdeOMzTuqsPfue8PmS06qQtfJ1lUzMzQt4hPKcBj_wWHsCGIjVO5ZmI1t/exec")
    suspend fun onSearch(@QueryMap params: Map<String, String>) : List<SearchResult>

    @POST("macros/s/AKfycbzpMt2j2JdcdeOMzTuqsPfue8PmS06qQtfJ1lUzMzQt4hPKcBj_wWHsCGIjVO5ZmI1t/exec")
    suspend fun onGetAll(@QueryMap params: Map<String, String>) : List<SearchResult>

    @POST("macros/s/AKfycbzpMt2j2JdcdeOMzTuqsPfue8PmS06qQtfJ1lUzMzQt4hPKcBj_wWHsCGIjVO5ZmI1t/exec")
    suspend fun getRecipe(@QueryMap params: Map<String, String>) : Recipe

    @POST("macros/s/AKfycbzpMt2j2JdcdeOMzTuqsPfue8PmS06qQtfJ1lUzMzQt4hPKcBj_wWHsCGIjVO5ZmI1t/exec")
    suspend fun getTodaysId(@QueryMap params: Map<String, String>) : Recipe
}