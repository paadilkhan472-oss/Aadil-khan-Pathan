package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiRepository
import com.example.model.ActionConfirmation
import com.example.model.ActionType
import com.example.model.AppLanguage
import com.example.model.ChatMessage
import com.example.model.LaunchableApp
import com.example.model.QuickCommand
import com.example.model.SystemTelemetry
import com.example.model.VisionScanResult
import com.example.service.SpeechService
import com.example.service.SystemActionsManager
import com.example.service.TtsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MainNavigationTab {
    HOME,
    CHAT,
    VISION,
    APPS,
    SETTINGS
}

class VisionAiViewModel(application: Application) : AndroidViewModel(application) {

    private val actionsManager = SystemActionsManager(application)
    private val speechService = SpeechService(application)
    private val ttsService = TtsService(application)
    private val aiRepository = AiRepository()

    // Active Navigation
    private val _currentTab = MutableStateFlow(MainNavigationTab.HOME)
    val currentTab: StateFlow<MainNavigationTab> = _currentTab.asStateFlow()

    // Language Preference
    private val _language = MutableStateFlow(AppLanguage.AUTO)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    // Chat Messages
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Telemetry
    private val _telemetry = MutableStateFlow(actionsManager.getSystemTelemetry())
    val telemetry: StateFlow<SystemTelemetry> = _telemetry.asStateFlow()

    // Speech State
    val isListening: StateFlow<Boolean> = speechService.isListening
    val rmsAmplitude: StateFlow<Float> = speechService.rmsAmplitude
    val recognizedText: StateFlow<String> = speechService.recognizedText
    val speechError: StateFlow<String?> = speechService.errorMessage

    // TTS State
    val isSpeaking: StateFlow<Boolean> = ttsService.isSpeaking
    val ttsPitch: StateFlow<Float> = ttsService.currentPitch
    val ttsSpeed: StateFlow<Float> = ttsService.currentSpeed
    val isMuted: StateFlow<Boolean> = ttsService.isMuted

    // Action Confirmation Modal
    private val _pendingAction = MutableStateFlow<ActionConfirmation?>(null)
    val pendingAction: StateFlow<ActionConfirmation?> = _pendingAction.asStateFlow()

    // Vision Scan
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _isAnalyzingImage = MutableStateFlow(false)
    val isAnalyzingImage: StateFlow<Boolean> = _isAnalyzingImage.asStateFlow()

    private val _scanResult = MutableStateFlow<VisionScanResult?>(null)
    val scanResult: StateFlow<VisionScanResult?> = _scanResult.asStateFlow()

    // Apps
    private val _installedApps = MutableStateFlow<List<LaunchableApp>>(emptyList())
    val installedApps: StateFlow<List<LaunchableApp>> = _installedApps.asStateFlow()

    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery.asStateFlow()

    // Quick Commands List
    val quickCommands = listOf(
        QuickCommand(
            id = "sys_check",
            titleEn = "System Diagnostics",
            titleHi = "सिस्टम स्थिति",
            promptEn = "Run full system telemetry diagnostics",
            promptHi = "पूरा सिस्टम डायग्नोस्टिक्स चलाओ",
            category = "SYSTEM",
            iconName = "speed"
        ),
        QuickCommand(
            id = "whatsapp_quick",
            titleEn = "Send WhatsApp",
            titleHi = "व्हाट्सएप भेजें",
            promptEn = "Send WhatsApp message to +919876543210 saying Hello",
            promptHi = "व्हाट्सएप पर +919876543210 को नमस्ते मैसेज भेजो",
            category = "COMM",
            iconName = "chat"
        ),
        QuickCommand(
            id = "call_quick",
            titleEn = "Voice Call",
            titleHi = "कॉल करें",
            promptEn = "Call 9112233445",
            promptHi = "9112233445 पर कॉल करो",
            category = "COMM",
            iconName = "phone"
        ),
        QuickCommand(
            id = "launch_yt",
            titleEn = "Open YouTube",
            titleHi = "यूट्यूब खोलें",
            promptEn = "Open YouTube",
            promptHi = "यूट्यूब खोलो",
            category = "APPS",
            iconName = "play"
        ),
        QuickCommand(
            id = "vision_cmd",
            titleEn = "Analyze Image",
            titleHi = "इमेज जांचें",
            promptEn = "Perform optical scan on loaded vision feed",
            promptHi = "कैमरा इमेज का विश्लेषण करो",
            category = "VISION",
            iconName = "camera"
        )
    )

    init {
        // Initialize welcome message
        val welcomeMsg = ChatMessage(
            text = "VISION AI [Stark Edition Mark L] online. Voice recognition and optical HUD ready. How may I assist you today, sir? (वॉयस कमांड या टाइपिंग द्वारा आदेश दें)",
            isUser = false,
            language = "en",
            sourceNote = "⚡ Vision AI Stark Core • Dual-language Hindi & English Voice Enabled"
        )
        _messages.value = listOf(welcomeMsg)

        // Load apps list
        refreshInstalledApps()

        // Setup speech result hook
        speechService.onSpeechResultListener = { recognized ->
            if (recognized.isNotBlank()) {
                sendUserMessage(recognized)
            }
        }
    }

    fun setTab(tab: MainNavigationTab) {
        _currentTab.value = tab
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = actionsManager.getInstalledApps()
        }
    }

    fun setAppSearchQuery(query: String) {
        _appSearchQuery.value = query
    }

    fun startListening() {
        speechService.startListening(_language.value)
    }

    fun stopListening() {
        speechService.stopListening()
    }

    fun toggleSpeechListening() {
        if (isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun speakText(text: String) {
        ttsService.speak(text, _language.value)
    }

    fun stopSpeaking() {
        ttsService.stop()
    }

    fun setPitch(pitch: Float) {
        ttsService.setPitch(pitch)
    }

    fun setSpeed(speed: Float) {
        ttsService.setSpeed(speed)
    }

    fun toggleMute() {
        ttsService.toggleMute()
    }

    fun sendUserMessage(text: String, imageUri: Uri? = null) {
        if (text.isBlank() && imageUri == null) return

        val userMessage = ChatMessage(
            text = text,
            isUser = true,
            language = if (_language.value == AppLanguage.HINDI) "hi" else "en",
            imageUri = imageUri
        )
        _messages.value = _messages.value + userMessage

        viewModelScope.launch {
            val response = aiRepository.processQuery(
                prompt = text,
                languagePreference = _language.value,
                imageUri = imageUri
            )

            val aiMessage = ChatMessage(
                text = response.replyText,
                isUser = false,
                language = response.language,
                suggestedAction = response.suggestedAction,
                sourceNote = response.sourceNote
            )
            _messages.value = _messages.value + aiMessage

            // If action was parsed, queue confirmation dialog
            if (response.suggestedAction != null) {
                _pendingAction.value = response.suggestedAction
            }

            // Speak response aloud if TTS active
            ttsService.speak(response.replyText, _language.value)
            _telemetry.value = actionsManager.getSystemTelemetry()
        }
    }

    fun confirmPendingAction() {
        val action = _pendingAction.value ?: return
        executeAction(action)
        _pendingAction.value = null
    }

    fun dismissPendingAction() {
        _pendingAction.value = null
    }

    fun triggerActionConfirmation(action: ActionConfirmation) {
        _pendingAction.value = action
    }

    private fun executeAction(action: ActionConfirmation) {
        when (action.type) {
            ActionType.WHATSAPP -> {
                actionsManager.executeWhatsAppMessage(
                    phone = action.payload,
                    message = action.details
                )
            }
            ActionType.PHONE_CALL -> {
                actionsManager.executePhoneCall(action.payload)
            }
            ActionType.LAUNCH_APP -> {
                if (!action.packageName.isNullOrBlank()) {
                    actionsManager.launchAppByPackage(action.packageName)
                } else {
                    actionsManager.launchAppByName(action.payload)
                }
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
        if (uri != null) {
            scanImage(uri)
        }
    }

    fun scanImage(uri: Uri) {
        viewModelScope.launch {
            _isAnalyzingImage.value = true
            val result = aiRepository.analyzeImage(uri)
            _scanResult.value = result
            _isAnalyzingImage.value = false

            // Also add to chat timeline
            val summaryText = if (_language.value == AppLanguage.HINDI) result.hindiSummary else result.summary
            ttsService.speak("Optical analysis complete. $summaryText", _language.value)
        }
    }

    fun clearSelectedImage() {
        _selectedImageUri.value = null
        _scanResult.value = null
    }

    fun launchApp(app: LaunchableApp) {
        actionsManager.launchAppByPackage(app.packageName)
    }

    fun refreshTelemetry() {
        _telemetry.value = actionsManager.getSystemTelemetry()
    }

    override fun onCleared() {
        super.onCleared()
        speechService.destroy()
        ttsService.shutdown()
    }
}
