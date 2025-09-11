package com.dreamyducks.navcook.ui.recipeViewer

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ViewerViewModel(): ViewModel() {
    val recipeManager = RecipeManager

    val recipe: StateFlow<Recipe?> = recipeManager.selectedRecipe
    private val _viewerUiState = MutableStateFlow(ViewerUiState())
    val viewerUiState : StateFlow<ViewerUiState> = _viewerUiState.asStateFlow()

    fun updateViewerUiState(newState: ViewerUiState) {
        _viewerUiState.value = newState
    }

    fun updateUiStateIndex(changeAmount: Int) {
        _viewerUiState.value = viewerUiState.value.copy(
            currentIndex = viewerUiState.value.currentIndex + changeAmount
        )
    }

    private var textToSpeech: TextToSpeech? = null
    fun textToSpeech(
        context: Context,
        text: String = "Text to speech",
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            textToSpeech = TextToSpeech(
                context
            ) {
                if (it == TextToSpeech.SUCCESS) {
                    textToSpeech?.let { txtToSpeech ->
                        txtToSpeech.language = Locale.US
                        txtToSpeech.setSpeechRate(0.8f)
                        txtToSpeech.speak(
                            text,
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

data class ViewerUiState(
    val currentIndex: Int = 0,
    val size: Int = 0,
    val audioScript: String = "",
    val isReadAround: Boolean = false,
    val isMicOn: Boolean = true,

)