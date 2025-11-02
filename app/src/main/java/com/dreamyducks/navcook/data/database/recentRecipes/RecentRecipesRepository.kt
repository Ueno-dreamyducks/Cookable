package com.dreamyducks.navcook.data.database.recentRecipes

import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.flow.Flow

interface RecentRecipesRepository {
    fun getAllRecentRecipesStream(): Flow<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe)
}