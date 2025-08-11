package com.dreamyducks.navcook.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dreamyducks.navcook.data.RecipeRepository
import com.dreamyducks.navcook.ui.recipeOverview.OverviewViewModel
import com.dreamyducks.navcook.ui.recipeViewer.ViewerViewModel
import com.dreamyducks.navcook.ui.search.SearchViewModel

class ViewModelFactory(
    private val recipeRepository: RecipeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        when {
            modelClass.isAssignableFrom(NavCookViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return NavCookViewModel(recipeRepository = recipeRepository) as T
            }

            modelClass.isAssignableFrom(OverviewViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return OverviewViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(ViewerViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return ViewerViewModel(recipeRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}