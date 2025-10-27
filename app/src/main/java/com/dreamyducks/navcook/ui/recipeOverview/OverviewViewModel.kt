package com.dreamyducks.navcook.ui.recipeOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamyducks.navcook.NavCookApplication
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipesRepository
import com.dreamyducks.navcook.data.database.recentRecipes.toReduced
import com.dreamyducks.navcook.network.Recipe
import com.dreamyducks.navcook.ui.homepage.HomepageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverviewViewModel(
    private val recentRecipesRepository: RecentRecipesRepository
) : ViewModel() {
    private val recipeManager = RecipeManager

    val recipe: StateFlow<Recipe?> = recipeManager.selectedRecipe

    init {
        viewModelScope.launch(Dispatchers.Default) {
            recipeManager.addLoadedRecipes(recipe.value!!)
            recentRecipesRepository.insertRecipe(recipe.value!!.toReduced())
        }
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NavCookApplication)
                val recentRecipesRepository = application.container.recentRecipeRepository
                OverviewViewModel(recentRecipesRepository = recentRecipesRepository)
            }
        }
    }
}

