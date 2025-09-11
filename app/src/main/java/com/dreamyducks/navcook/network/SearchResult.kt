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

@Serializable
data class Recipe(
    val id: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val ingredients: List<String>,
    val steps: List<Step>
)

@Serializable
data class Step(
    val step: String = "",
    val title: String = "",
    val image: String? = null,
    val ingredients: List<String>? = null,
    val description: String? = null
)
