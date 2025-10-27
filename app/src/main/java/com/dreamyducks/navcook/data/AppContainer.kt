package com.dreamyducks.navcook.data

import android.content.Context
import com.dreamyducks.navcook.data.database.recentRecipes.OfflineRecentRecipesRepository
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipesDatabase
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipesRepository
import com.dreamyducks.navcook.data.database.searchQueries.OfflineSearchQueriesRepository
import com.dreamyducks.navcook.data.database.searchQueries.SearchQueriesDatabase
import com.dreamyducks.navcook.data.database.searchQueries.SearchQueriesRepository
import com.dreamyducks.navcook.network.NavCookApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val searchRepository: SearchRepository
    val searchQueriesRepository: SearchQueriesRepository
    val recentRecipeRepository: RecentRecipesRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {
    private val BASE_URL =
        "https://script.google.com"

    private val json = Json { ignoreUnknownKeys = true }
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService : NavCookApiService by lazy {
        retrofit.create(NavCookApiService::class.java)
    }

    override val searchRepository: SearchRepository by lazy {
        NetworkSearchRepository(retrofitService)
    }

    //search queries repo.
    override val searchQueriesRepository: SearchQueriesRepository by lazy {
        OfflineSearchQueriesRepository(SearchQueriesDatabase.getDatabase(context).searchDao())
    }

    override val recentRecipeRepository: RecentRecipesRepository by lazy {
        OfflineRecentRecipesRepository(RecentRecipesDatabase.getDatabase(context).recentRecipeDao())
    }
}