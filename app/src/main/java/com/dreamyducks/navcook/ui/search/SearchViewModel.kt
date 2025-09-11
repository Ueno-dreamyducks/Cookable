package com.dreamyducks.navcook.ui.search

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val recipeManager = RecipeManager
    var searchUiState: SearchUiState by mutableStateOf(SearchUiState.None)
        private set

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    override fun onCleared() {
        super.onCleared()
        //clear searchUiState
        searchUiState = SearchUiState.None
    }

    fun resetSearchUiState() {
        searchUiState = SearchUiState.None
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    suspend fun onSearch() {
        searchUiState = SearchUiState.Loading
        //Create function connect to internet to get result
        val params = mutableMapOf<String, String>()
        params["search"] = searchQuery.value

        val result = searchRepository.onSearch(params)

        recipeManager.updateFoundRecipes(result)

        searchUiState = SearchUiState.Success

//        if (/*success*/ true) {
//            val searchResult = SearchResult(
//                searchQuery = searchQuery.value,
//                recipes = listOf<Recipe>(
//                    Recipe(
//                        id = 0,
//                        title = "Pasta",
//                        thumbNailImage = R.drawable.pasta,
//                        ingredients = listOf<String>(
//                            "Pasta",
//                            "Tomato Paste",
//                            "Meat Ball"
//                        )
//                    ),
//                    Recipe(
//                        id = 1,
//                        title = "Title of Recipe",
//                        thumbNailImage = R.drawable.pasta,
//                    ),
//                    Recipe(
//                        id = 2,
//                        title = "Title of Recipe",
//                        thumbNailImage = R.drawable.pasta,
//                    ),
//                    Recipe(
//                        id = 3,
//                        title = "Title of Recipe 2",
//                        thumbNailImage = R.drawable.pasta
//                    )
//                )
//            )
//
//            recipeManager.updateFoundRecipes(searchResult)
//            searchUiState = SearchUiState.Success
//        } else {
//            searchUiState = SearchUiState.Error
//        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NavCookApplication)
                val searchRepository = application.container.searchRepository
                SearchViewModel(searchRepository = searchRepository)
            }
        }
    }
}

sealed interface SearchUiState {
    object None : SearchUiState
    object Loading : SearchUiState
    object Error : SearchUiState

    //data class Success(val recipes: List<Recipe>) : SearchUiState
    object Success : SearchUiState
}

