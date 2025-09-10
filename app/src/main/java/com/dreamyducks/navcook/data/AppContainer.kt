package com.dreamyducks.navcook.data

import com.dreamyducks.navcook.network.NavCookApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val searchRepository: SearchRepository
}

class DefaultAppContainer: AppContainer {
    private val BASE_URL =
        "https://script.google.com"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .build()

    private val retrofitService : NavCookApiService by lazy {
        retrofit.create(NavCookApiService::class.java)
    }

    override val searchRepository: SearchRepository by lazy {
        NetworkSearchRepository(retrofitService)
    }
}