package com.dreamyducks.navcook.data.database.recentRecipes

import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.flow.Flow

class OfflineRecentRecipesRepository(private val recentRecipeDao: RecentRecipeDao) : RecentRecipesRepository {
    override fun getAllRecentRecipesStream(): Flow<List<Recipe>> = recentRecipeDao.getAllRecentRecipesStream()
    override suspend fun insertRecipe(recipe: Recipe) = recentRecipeDao.insert(recipe)
}