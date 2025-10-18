package com.dreamyducks.navcook.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamyducks.navcook.NavCookApplication
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.data.SearchRepository
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomepageViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val recipeManager = RecipeManager
    private val _homepageUiState = MutableStateFlow(HomepageUiState())
    val homepageUiState: StateFlow<HomepageUiState> = _homepageUiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTodaysRecipe()
        }
    }

    suspend fun getTodaysRecipe() {
        val params = mutableMapOf<String, String>()
        params["todaysRecipe"] = "0" //the key matter not value(value not used for this service)

        try {
            val todaysRecipe = searchRepository.getTodaysRecipe(params)

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

    fun onGetRecipeDetail() {
        if(homepageUiState.value.todaysRecipeState is TodaysRecipeState.Success) {
            recipeManager.updateSelectedRecipe((homepageUiState.value.todaysRecipeState as TodaysRecipeState.Success).recipe)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as NavCookApplication)
                val searchRepository = application.container.searchRepository
                HomepageViewModel(searchRepository = searchRepository)
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