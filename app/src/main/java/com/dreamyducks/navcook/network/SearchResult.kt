package com.dreamyducks.navcook.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult (
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val ingredients: String = "",
    val steps: String = "",
    val tags: String = "",
    val time: Int = 0,
)

@Serializable
data class Recipe(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val ingredients: List<Ingredient>,
    val steps: List<Step>,
    val tags: String = "",
    val time: Int = 0
)

@Serializable
data class Ingredient(
    val name: String = "",
    val unit: String = "",
    val amount : Int? = null
)
@Serializable
data class Step(
    val step: String = "",
    val title: String = "",
    @SerialName(value="imageId")
    val image: String? = null,
    val ingredients: List<String>? = null,
    val description: String? = null
)
