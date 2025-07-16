package com.dreamyducks.navcook.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.R
import com.dreamyducks.navcook.data.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale


class NavCookViewModel() : ViewModel() {
    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    var searchUiState: SearchUiState by mutableStateOf(SearchUiState.Loading)
        private set

    private val _recipeUiState = MutableStateFlow<Recipe?>(/*null*/ Recipe(
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

    private var textToSpeech: TextToSpeech? = null

    fun updateSearch(newValue: String) {
        _searchInput.value = newValue
    }

    fun onSearch(
        context: Context
    ) {
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
        context: Context,
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
    }

    fun textToSpeech(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            textToSpeech = TextToSpeech(
                context
            ) {
                if (it == TextToSpeech.SUCCESS) {
                    textToSpeech?.let { txtToSpeech ->
                        txtToSpeech.language = Locale.US
                        txtToSpeech.setSpeechRate(0.8f)
                        txtToSpeech.speak(
                            "Preheat oven to 350 degrees Fahrenheit. Spray a 8x8 baking dish with baking spray.",
                            TextToSpeech.QUEUE_ADD,
                            null,
                            null
                        )
                    }
                }
            }
        }

    }

    fun stopTextToSpeech() {
        viewModelScope.launch(Dispatchers.IO) {
            textToSpeech?.stop()
        }
    }
}

sealed interface SearchUiState {
    object Loading : SearchUiState
    object Error : SearchUiState
    data class Success(val recipes: List<Recipe>) : SearchUiState
}

