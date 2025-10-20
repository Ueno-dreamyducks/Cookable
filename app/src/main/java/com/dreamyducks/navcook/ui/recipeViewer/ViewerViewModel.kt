package com.dreamyducks.navcook.ui.recipeViewer

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import java.util.Locale

class ViewerViewModel() : ViewModel() {
    val recipeManager = RecipeManager

    val recipe: StateFlow<Recipe?> = recipeManager.selectedRecipe
    private val _viewerUiState = MutableStateFlow(ViewerUiState())
    val viewerUiState: StateFlow<ViewerUiState> = _viewerUiState.asStateFlow()

    private var model: Model? = null
    private var speechService: SpeechService? = null

    fun initVosk(context: Context) {
        StorageService.unpack(
            context, "vosk-model-small-en-us-0.15", "model",
            { unpackedModel ->
                model = unpackedModel
                startListening(context)
            },
            { exception ->
                Log.e("VOSK", "Failed to unpack ${exception.message}")
                throw exception
            }
        )
    }
    private fun startListening(context: Context) {
        val recognizer = Recognizer(model, 16000.0f)

        speechService = SpeechService(recognizer, 16000.0f)

        speechService?.startListening(object : RecognitionListener {

            override fun onPartialResult(hypothesis: String?) {

                Log.d("Vosk Partial", hypothesis ?: "")

                // Execute your code inside the check
                if ("".any{ hypothesis?.contains(it, ignoreCase = true) == true })
                {
                    Toast.makeText(context, " Request Word detected", Toast.LENGTH_SHORT).show()
                    pause()
                }
            }

            override fun onResult(hypothesis: String?) {}
            override fun onFinalResult(hypothesis: String?) {}
            override fun onError(e: Exception?) {
                Log.e("VoskError", e.toString())
            }

            override fun onTimeout() {}
        })
    }
    fun pause() {
        speechService?.stop()
        speechService?.shutdown()
        speechService = null
        //isRunning = false
        Log.d("Voice Recognition", "Paused")
    }

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
    val isMicOn: Boolean = false,
    val isShowStatusBar: Boolean = false
)