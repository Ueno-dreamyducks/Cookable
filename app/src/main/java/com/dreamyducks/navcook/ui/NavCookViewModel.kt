package com.dreamyducks.navcook.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.R
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
                            title = "Title of Recipe",
                            thumbNailImage = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.pasta
                            ).asImageBitmap()
                        ),
                        Recipe(
                            title = "Title of Recipe",
                            thumbNailImage = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.pasta
                            ).asImageBitmap()
                        ),
                        Recipe(
                            title = "Title of Recipe",
                            thumbNailImage = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.pasta
                            ).asImageBitmap()
                        ),
                        Recipe(
                            title = "Title of Recipe 2",
                            thumbNailImage = BitmapFactory.decodeResource(
                                context.resources,
                                R.drawable.soup
                            ).asImageBitmap()
                        )
                    )

                )
            }
        }
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

data class Recipe(
    val title: String,
    val thumbNailImage: ImageBitmap
)