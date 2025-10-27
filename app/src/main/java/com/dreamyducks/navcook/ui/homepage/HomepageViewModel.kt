package com.dreamyducks.navcook.ui.homepage

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamyducks.navcook.NavCookApplication
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.SearchRepository
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipe
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipesRepository
import com.dreamyducks.navcook.data.database.searchQueries.Query
import com.dreamyducks.navcook.data.database.searchQueries.SearchQueriesRepository
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

class HomepageViewModel(
    private val searchRepository: SearchRepository,
    private val searchQueriesRepository: SearchQueriesRepository,
    private val recentRecipesRepository: RecentRecipesRepository,
) : ViewModel() {
    private val recipeManager = RecipeManager
    private val _homepageUiState = MutableStateFlow(HomepageUiState())
    val homepageUiState: StateFlow<HomepageUiState> = _homepageUiState.asStateFlow()

    val queriesState: StateFlow<List<Query>> =
        searchQueriesRepository.getAllSearchQueriesStream().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(Query(0, ""))
            )

    val recentRecipesState: StateFlow<List<RecentRecipe>> =
        recentRecipesRepository.getAllRecentRecipesStream().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(RecentRecipe())
            )

    init {
        viewModelScope.launch {
            getTodaysRecipe()
        }
    }

    suspend fun getTodaysRecipe() {
        withContext(Dispatchers.Default) {
            val params = mutableMapOf<String, String>()
            params["todaysRecipe"] = "0" //the key matter not value(value not used for this service)

            try {
                val todaysRecipe = searchRepository.getTodaysRecipe(params)
                Log.d("Recipe API", todaysRecipe.toString())

                _homepageUiState.value = homepageUiState.value.copy(
                    todaysRecipeState = TodaysRecipeState.Success(todaysRecipe),
                    todaysRecipeId = todaysRecipe.id
                )
            } catch (e: Exception) {
                _homepageUiState.value = homepageUiState.value.copy(
                    todaysRecipeState = TodaysRecipeState.Error
                )
            }
        }
    }

    fun onGetRecipeDetail() {
        if (homepageUiState.value.todaysRecipeState is TodaysRecipeState.Success) {
            recipeManager.updateSelectedRecipe((homepageUiState.value.todaysRecipeState as TodaysRecipeState.Success).recipe)
        }
    }

    suspend fun onGetRecipeById(id: Int, context: Context) : Boolean {
        try {
            val params = mutableMapOf<String, String>()
            params["recipeId"] = id.toString()
            val result = searchRepository.getRecipe(params)
            recipeManager.updateSelectedRecipe(result)
            return true
        } catch (e: IOException) {
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
            return false
        } catch (e: Exception) {
            Toast.makeText(context, "An error occupied", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NavCookApplication)
                val searchRepository = application.container.searchRepository
                val queriesRepository = application.container.searchQueriesRepository
                val recentRecipesRepository = application.container.recentRecipeRepository
                HomepageViewModel(
                    searchRepository = searchRepository,
                    searchQueriesRepository = queriesRepository,
                    recentRecipesRepository = recentRecipesRepository
                )
            }
        }
    }
}

data class HomepageUiState(
    val todaysRecipeId: Int? = null,
    val todaysRecipeState: TodaysRecipeState = TodaysRecipeState.Loading
)

sealed interface TodaysRecipeState {
    object Loading : TodaysRecipeState
    data class Success(val recipe: Recipe) : TodaysRecipeState
    object Error : TodaysRecipeState
}