package com.dreamyducks.navcook.ui.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.data.Recipe
import com.dreamyducks.navcook.data.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {
    var searchUiState: SearchUiState by mutableStateOf(SearchUiState.Loading)
        private set

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    private val _recipeUiState = MutableStateFlow<Recipe?>(/*null*/
        Recipe(
            id = 0,
            title = "Pasta",
            thumbNailImage = R.drawable.pasta,
            ingredients = listOf<String>(
                "Pasta",
                "Tomato Paste",
                "Meat Ball"
            )
        )
    )
    val recipeUiState: StateFlow<Recipe?> = _recipeUiState.asStateFlow()

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            searchUiState = SearchUiState.Loading
            //Create function connect to internet to get result
            delay(5000L) //Simulate loading
            if (/*success*/ true) {
                searchUiState = SearchUiState.Success(
                    listOf<Recipe>(
                        Recipe(
                            id = 0,
                            title = "Pasta",
                            thumbNailImage = R.drawable.pasta,
                            ingredients = listOf<String>(
                                "Pasta",
                                "Tomato Paste",
                                "Meat Ball"
                            )
                        ),
                        Recipe(
                            id = 1,
                            title = "Title of Recipe",
                            thumbNailImage = R.drawable.pasta,
                        ),
                        Recipe(
                            id = 2,
                            title = "Title of Recipe",
                            thumbNailImage = R.drawable.pasta,
                        ),
                        Recipe(
                            id = 3,
                            title = "Title of Recipe 2",
                            thumbNailImage = R.drawable.pasta
                        )
                    )
                )
            }
        }
    }
    fun onGetRecipeDetail(
        id: Int,
    ) {
        _recipeUiState.value = null //reset selected recipe
        //Create database access to search id

        Log.d("MainActivity", "Selected recipe id: $id")

        _recipeUiState.value = Recipe(
            id = 0,
            title = "Pasta",
            thumbNailImage = R.drawable.pasta,
            ingredients = listOf<String>(
                "Pasta",
                "Tomato Paste",
                "Meat Ball"
            )
        )

        recipeRepository.updateSelectedRecipe(recipeUiState.value!!)
    }
}

sealed interface SearchUiState {
    object Loading : SearchUiState
    object Error : SearchUiState
    data class Success(val recipes: List<Recipe>) : SearchUiState
}