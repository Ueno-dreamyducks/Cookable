package com.dreamyducks.navcook.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamyducks.navcook.NavCookApplication
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.SearchRepository
import com.dreamyducks.navcook.network.SearchResult
import kotlinx.coroutines.flow.StateFlow

class SearchResultViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val recipeManager = RecipeManager
    val searchResult: StateFlow<List<SearchResult>> = recipeManager.foundRecipes

    suspend fun onGetRecipeDetail(
        id: Int,
    ) {
        //Create database access to search id
        Log.d("MainActivity", "Selected recipe id: $id")
        val params = mutableMapOf<String, String>()
        params["recipeId"] = "1"
        Log.d("MainActivity", "params set")

        val result = searchRepository.getRecipe(params)
        Log.d("MainActivity", "From recipe id: " + result.toString())
        recipeManager.updateSelectedRecipe(result)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NavCookApplication)
                val searchRepository = application.container.searchRepository
                SearchResultViewModel(searchRepository = searchRepository)
            }
        }
    }
}