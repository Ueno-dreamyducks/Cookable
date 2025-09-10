package com.dreamyducks.navcook.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.data.Recipe
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.network.SearchResult
import kotlinx.coroutines.flow.StateFlow

class SearchResultViewModel() : ViewModel() {
    private val recipeManager = RecipeManager
    val searchResult: StateFlow<List<SearchResult>> = recipeManager.foundRecipes

    fun onGetRecipeDetail(
        id: Int,
    ) {
        //Create database access to search id

        Log.d("MainActivity", "Selected recipe id: $id")

        val selectedRecipe = Recipe(
            id = 0,
            title = "Pasta",
            thumbNailImage = R.drawable.pasta,
            ingredients = listOf<String>(
                "Pasta",
                "Tomato Paste",
                "Meat Ball"
            )
        )

        recipeManager.updateSelectedRecipe(selectedRecipe)
    }
}