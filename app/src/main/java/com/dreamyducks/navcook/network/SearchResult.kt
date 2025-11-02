package com.dreamyducks.navcook.network

import androidx.room.Entity
import androidx.room.PrimaryKey
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
@Entity(tableName = "recent_recipes")
data class Recipe(
    @PrimaryKey
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
    val amount : Double? = null
)
@Serializable
data class Step(
    val step: String = "",
    val title: String = "",
    @SerialName(value="imageId")
    val image: String? = null,
    val ingredients: List<String>? = null,
    val description: String? = null,
    val askable: String? = null
)
