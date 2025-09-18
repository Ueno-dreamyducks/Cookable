package com.dreamyducks.navcook.ui.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var searchResultScreen: SearchResultScreen by mutableStateOf(SearchResultScreen.Result)
        private set

    fun updateScreen(screen: SearchResultScreen) {
        searchResultScreen = screen
    }
    private val recipeManager = RecipeManager
    val searchQuery: StateFlow<String> = recipeManager.searchQuery
    val searchResult: StateFlow<List<SearchResult>> = recipeManager.foundRecipes

    suspend fun onGetRecipeDetail(
        id: Int,
    ) {
        updateScreen(SearchResultScreen.LoadingRecipe) //show loading screen on app
        //Create database access to search id
        val params = mutableMapOf<String, String>()
        params["recipeId"] = id.toString()

        val result = searchRepository.getRecipe(params)
        Log.d("MainActivity", result.toString());
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

sealed interface SearchResultScreen {
    object Result : SearchResultScreen
    object LoadingRecipe : SearchResultScreen
}