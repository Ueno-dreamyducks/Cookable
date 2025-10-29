package com.dreamyducks.navcook.ui.search

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamyducks.navcook.NavCookApplication
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.SearchRepository
import com.dreamyducks.navcook.network.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException

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

        val loadedRecipes = recipeManager.loadedRecipes

        val recipeOrNull = loadedRecipes.value.getOrDefault(id, null)

        if(recipeOrNull == null) { //recipe was not loaded yet
            try {
                val params = mutableMapOf<String, String>()
                params["recipeId"] = id.toString()
                Log.d("MainActivity", params.toString())
                val result = searchRepository.getRecipe(params)
                Log.d("MainActivity", result.toString());
                recipeManager.updateSelectedRecipe(result)
            } catch (e: IOException) {
                updateScreen(SearchResultScreen.Result)
            } catch(e: Exception) {
                updateScreen(SearchResultScreen.Result)
            }
        } else { //the recipe already loaded on recipeManager
            recipeManager.updateSelectedRecipe(recipeOrNull)
        }
    }

    suspend fun onGetAll(
        context: Context,
    ) {
        updateScreen(SearchResultScreen.LoadingRecipe)

        val params = mutableMapOf<String, String>()
        params["getAll"] = ""

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = searchRepository.onGetAll(params)

                recipeManager.updateSearchQuery("All")
                recipeManager.updateFoundRecipes(result)

                updateScreen(SearchResultScreen.Result)
            } catch(e: IOException) {
                Toast.makeText(context, "Internet Error", Toast.LENGTH_SHORT).show()
            } catch(e: Exception) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
        }
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