package com.dreamyducks.navcook.ui.recipeOverview

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dreamyducks.navcook.data.Recipe
import com.dreamyducks.navcook.data.RecipeRepository
import kotlinx.coroutines.flow.StateFlow

class OverviewViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {
    init {
        Log.d("MainActivity", recipeRepository.selectedRecipe.value
            !!.title)
    }
    val recipe: StateFlow<Recipe?> = recipeRepository.selectedRecipe
}