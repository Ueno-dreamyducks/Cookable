package com.dreamyducks.navcook.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap

data class Recipe(
    val id: Int,
    val title: String,
    @DrawableRes
    val thumbNailImage: Int,
    val ingredients: List<String>? = null
)