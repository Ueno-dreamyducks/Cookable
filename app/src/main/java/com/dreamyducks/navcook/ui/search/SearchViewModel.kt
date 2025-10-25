package com.dreamyducks.navcook.ui.search

import android.util.Log
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
import com.dreamyducks.navcook.data.Query
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.SearchQueriesRepository
import com.dreamyducks.navcook.data.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val searchQueriesRepository: SearchQueriesRepository,
) : ViewModel() {
    private val recipeManager = RecipeManager
    var searchUiState: SearchUiState by mutableStateOf(SearchUiState.None)
        private set

    private val _searchQuery = MutableStateFlow<String>("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val queriesState: StateFlow<List<Query>> =
        searchQueriesRepository.getAllSearchQueriesStream().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(Query(0, ""))
            )

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
        params["search"] = searchQuery.value.trim()

        viewModelScope.launch(Dispatchers.Default) {
            addQuery()
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = searchRepository.onSearch(params)

                recipeManager.updateSearchQuery(searchQuery.value)
                recipeManager.updateFoundRecipes(result)

                searchUiState = SearchUiState.Success
            } catch (e: IOException) {
                searchUiState = SearchUiState.NoInternet
            } catch (e: Exception) {
                Log.e("MainActivity", e.toString())
                searchUiState = SearchUiState.Error
            }
        }

    }

    suspend fun addQuery() {
        searchQueriesRepository.insertQuery(Query(id=0, query = searchQuery.value))
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NavCookApplication)
                val searchRepository = application.container.searchRepository
                val queriesRepository = application.container.searchQueriesRepository
                SearchViewModel(searchRepository = searchRepository, searchQueriesRepository = queriesRepository)
            }
        }
    }
}

sealed interface SearchUiState {
    object None : SearchUiState
    object Loading : SearchUiState
    object Error : SearchUiState

    object NoInternet : SearchUiState

    //data class Success(val recipes: List<Recipe>) : SearchUiState
    object Success : SearchUiState
}

