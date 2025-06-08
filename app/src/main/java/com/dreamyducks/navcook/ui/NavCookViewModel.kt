package com.dreamyducks.navcook.ui

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale


class NavCookViewModel() : ViewModel() {
    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private var textToSpeech: TextToSpeech? = null

    fun updateSearch(newValue: String) {
        _searchInput.value = newValue
    }

    fun tts(context: Context) {
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