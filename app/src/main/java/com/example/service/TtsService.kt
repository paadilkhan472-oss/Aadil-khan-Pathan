package com.example.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsService(private val context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "TtsService"

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentPitch = MutableStateFlow(1.0f)
    val currentPitch: StateFlow<Float> = _currentPitch.asStateFlow()

    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    Log.w(TAG, "TTS Utterance error: $errorCode")
                }
            })
            // Default to English
            tts?.language = Locale.US
            tts?.setPitch(_currentPitch.value)
            tts?.setSpeechRate(_currentSpeed.value)
        } else {
            Log.e(TAG, "TextToSpeech initialization failed with status $status")
        }
    }

    fun speak(text: String, preferredLanguage: AppLanguage = AppLanguage.AUTO) {
        if (_isMuted.value) return
        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not initialized yet")
            return
        }

        // Detect if text contains Hindi / Devanagari script
        val isDevanagari = text.any { it in '\u0900'..'\u097F' }
        val targetLocale = when {
            preferredLanguage == AppLanguage.HINDI -> Locale("hi", "IN")
            preferredLanguage == AppLanguage.ENGLISH -> Locale.US
            isDevanagari -> Locale("hi", "IN")
            else -> Locale.US
        }

        val langResult = tts?.setLanguage(targetLocale)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "Locale $targetLocale not supported, falling back to US English")
            tts?.language = Locale.US
        }

        tts?.setPitch(_currentPitch.value)
        tts?.setSpeechRate(_currentSpeed.value)

        val utteranceId = "stark_tts_${System.currentTimeMillis()}"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop TTS", e)
        }
    }

    fun setPitch(pitch: Float) {
        _currentPitch.value = pitch.coerceIn(0.5f, 2.0f)
        tts?.setPitch(_currentPitch.value)
    }

    fun setSpeed(speed: Float) {
        _currentSpeed.value = speed.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(_currentSpeed.value)
    }

    fun toggleMute() {
        val newMuted = !_isMuted.value
        _isMuted.value = newMuted
        if (newMuted) {
            stop()
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (_: Exception) {}
    }
}
