package com.dreamyducks.navcook.ui.homepage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamyducks.navcook.NavCookApplication
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.SearchRepository
import com.dreamyducks.navcook.data.database.recentRecipes.RecentRecipesRepository
import com.dreamyducks.navcook.data.database.searchQueries.Query
import com.dreamyducks.navcook.data.database.searchQueries.SearchQueriesRepository
import com.dreamyducks.navcook.network.Ingredient
import com.dreamyducks.navcook.network.Recipe
import com.dreamyducks.navcook.network.Step
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    val recentRecipesState: StateFlow<List<Recipe>> =
        recentRecipesRepository.getAllRecentRecipesStream().map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = listOf(
                    Recipe(
                        ingredients = listOf(Ingredient()),
                        steps = listOf(Step())
                    ),
                )
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

    fun setRecipe(recipe: Recipe) {
        recipeManager.updateSelectedRecipe(recipe)
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