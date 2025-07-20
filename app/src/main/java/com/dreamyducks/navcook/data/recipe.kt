package com.dreamyducks.navcook.data

import androidx.annotation.DrawableRes

data class Recipe(
    val id: Int,
    val title: String,
    @DrawableRes
    val thumbNailImage: Int,
    val ingredients: List<String>? = null,
    val steps: List<Step>? = null
)

data class Step(
    val title: String,
    @DrawableRes
    val image: Int?,
    val ingredients: List<String>?,
    val description: String,
)