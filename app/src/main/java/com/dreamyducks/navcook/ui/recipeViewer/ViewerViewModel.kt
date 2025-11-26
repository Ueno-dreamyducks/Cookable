package com.dreamyducks.navcook.ui.recipeViewer

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamyducks.navcook.data.RecipeManager
import com.dreamyducks.navcook.network.Recipe
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    //vosk voice recognition
    private var model: Model? = null
    private var speechService: SpeechService? = null

    //Tool menu state
    private val _toolMenuState = MutableStateFlow<ToolMenuState>(ToolMenuState.None)
    val toolMenuState: StateFlow<ToolMenuState> = _toolMenuState.asStateFlow()
    private val _titleResId = MutableStateFlow<Int?>(null)
    val titleResId : StateFlow<Int?> = _titleResId.asStateFlow()
    private val _showMenu = MutableStateFlow(false)
    val showMenu : StateFlow<Boolean> = _showMenu.asStateFlow()

    //gemini
    //Do not activate paid version.
    private val aiModel = GenerativeModel(
        "gemini-2.5-flash",
        apiKey = "AIzaSyBdaBVs0aMk3KJo0pfjbE5gg_uqHER1IuM",
        generationConfig = generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
            responseMimeType = "text/plain"
        },
    )

    private val _askableInput = MutableStateFlow<Bitmap?>(null)
    val askableInput: StateFlow<Bitmap?> = _askableInput.asStateFlow()
    private val _isCoolDown = MutableStateFlow(false)
    private var isCoolDown: StateFlow<Boolean> = _isCoolDown.asStateFlow()

    //gemini; askable
    fun generateAskable() {
        if(askableInput.value != null ) { // check if image is in
            viewModelScope.launch(Dispatchers.IO) {
                updateViewerUiState(viewerUiState.value.copy(askableRes = "")) //reset askable response
                val question = recipe.value!!.steps[viewerUiState.value.currentIndex].askable
                val inputContent = content {
                    image(askableInput.value!!)
                    text("Answer in two sentences: First, just yes/no; then write a sentence summary of is it to this: $question")
                }

                val response =
                    aiModel.generateContent(inputContent)

                updateViewerUiState(
                    viewerUiState.value.copy(askableRes = response.text.orEmpty())
                )
            }
        }
    }

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

    //vosk voice recognition
    private fun startListening(context: Context) {
        val stopTerms = listOf("stop", "pause")
        val nextTerm = listOf("next step", "next")
        val previousTerm = listOf("back", "go back", "previous", "previous step")
        val recognizer = Recognizer(model, 16000.0f)

        var actionExecuted = false;

        speechService = SpeechService(recognizer, 16000.0f)

        updateViewerUiState(_viewerUiState.value.copy(isMicOn = true))
        speechService?.startListening(object : RecognitionListener {


            override fun onPartialResult(hypothesis: String?) {
                Log.d("Vosk Partial", hypothesis ?: "")

                // Execute your code inside the check
                //if (hypothesis?.contains("stop pause", ignoreCase = true) == true )
                if (stopTerms.any { hypothesis?.contains(it, ignoreCase = true) ?: false }) {
                    pause()
                } else if (nextTerm.any {
                        hypothesis?.contains(it, ignoreCase = true) ?: false
                    } && !actionExecuted) {
                    actionExecuted = true
                    updateUiStateIndex(1)
                } else if (previousTerm.any {
                        hypothesis?.contains(it, ignoreCase = true) ?: false
                    } && !actionExecuted) {
                    updateUiStateIndex(-1)
                    actionExecuted = true
                }
            }

            override fun onResult(hypothesis: String?) {
                Log.d("Vosk", hypothesis ?: "")

                actionExecuted = false

                val json = JSONObject(hypothesis ?: "")

                updateViewerUiState(
                    viewerUiState.value.copy(
                        transcript = json.getString("text")
                    )
                )
            }

            override fun onFinalResult(hypothesis: String?) {}
            override fun onError(e: Exception?) {
                Log.e("VoskError", e.toString())
            }

            override fun onTimeout() {}
        })
    }

    //vosk voice recognition
    fun pause() {
        viewModelScope.launch(Dispatchers.Default) {
            updateViewerUiState(
                _viewerUiState.value.copy(
                    isMicOn = false,
                    transcript = "",
                    isShowMenu = false
                )
            )

            speechService?.stop()
            speechService?.shutdown()
            speechService = null
            //isRunning = false
            Log.d("Voice Recognition", "Paused")
        }
    }

    fun updateViewerUiState(newState: ViewerUiState) {
        _viewerUiState.value = newState
    }

    fun updateAskableInput(new: Bitmap) {
        _askableInput.value = new
    }

    fun updateUiStateIndex(changeAmount: Int) {
        if (_isCoolDown.value) {
            return
        }
        viewModelScope.launch {
            _isCoolDown.update { true }

            if (viewerUiState.value.currentIndex + changeAmount >= 0 && viewerUiState.value.currentIndex + changeAmount < viewerUiState.value.size + 1) {
                _viewerUiState.update { current ->
                    current.copy(
                        currentIndex = current.currentIndex + changeAmount,
                        askableRes = "" //reset askable response
                    )
                }
            }

            delay(1000)
            _isCoolDown.update { false }
        }
    }

    fun changeMenuState(
        newState: ToolMenuState? = ToolMenuState.None,
        title: Int? = null
    ) {
        if(newState == null || newState == toolMenuState.value ) {
            _showMenu.update { false }
            _toolMenuState.update {
                ToolMenuState.None
            }
            _titleResId.update { null }
        } else {
            _showMenu.update { true }
            _toolMenuState.update {
                newState
            }
            _titleResId.update { title }
        }
    }

    private
    var textToSpeech: TextToSpeech? = null
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
    val isShowMenu: Boolean = false,
    val isReadAround: Boolean = false,
    val isMicOn: Boolean = false,
    val isShowStatusBar: Boolean = false,
    val transcript: String = "",
    val askableRes: String = ""
)

sealed interface ToolMenuState {
    object None : ToolMenuState
    object CameraView : ToolMenuState
    object MicView : ToolMenuState
    object Menu : ToolMenuState
    object RecordPermission : ToolMenuState
    object CameraPermission : ToolMenuState
}