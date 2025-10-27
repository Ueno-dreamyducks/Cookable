package com.dreamyducks.navcook.data.database.recentRecipes

import com.dreamyducks.navcook.data.database.searchQueries.Query
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.flow.Flow

interface RecentRecipesRepository {
    fun getAllRecentRecipesStream(): Flow<List<RecentRecipe>>
    suspend fun insertRecipe(recipe: RecentRecipe)
}