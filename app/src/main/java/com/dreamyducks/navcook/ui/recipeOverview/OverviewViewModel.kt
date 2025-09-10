package com.dreamyducks.navcook.ui.recipeOverview

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dreamyducks.navcook.data.Recipe
import com.dreamyducks.navcook.data.RecipeManager
import kotlinx.coroutines.flow.StateFlow

class OverviewViewModel() : ViewModel() {
    private val recipeManager = RecipeManager

    init {
        Log.d("MainActivity", recipeManager.selectedRecipe.value
            !!.title)
    }
    val recipe: StateFlow<Recipe?> = recipeManager.selectedRecipe
}