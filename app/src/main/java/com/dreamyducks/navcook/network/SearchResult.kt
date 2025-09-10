package com.dreamyducks.navcook.network

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult (
    val id: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val ingredients: String = "",
    val steps: String = ""
)