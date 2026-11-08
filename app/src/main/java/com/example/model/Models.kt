package com.example.model

import android.graphics.Bitmap
import android.net.Uri

enum class AppLanguage(val code: String, val label: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी"),
    AUTO("auto", "Auto (द्विभाषी)")
}

enum class ActionType {
    WHATSAPP,
    PHONE_CALL,
    LAUNCH_APP
}

data class ActionConfirmation(
    val type: ActionType,
    val title: String,
    val target: String,
    val details: String,
    val payload: String = "",
    val packageName: String? = null
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "en",
    val imageUri: Uri? = null,
    val suggestedAction: ActionConfirmation? = null,
    val isSpeaking: Boolean = false,
    val sourceNote: String? = null
)

data class QuickCommand(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val promptEn: String,
    val promptHi: String,
    val category: String,
    val iconName: String
)

data class LaunchableApp(
    val packageName: String,
    val appName: String,
    val isPopular: Boolean = false,
    val category: String = "Utility"
)

data class VisionScanResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val summary: String,
    val detectedObjects: List<String>,
    val tacticalAssessment: String,
    val confidenceScore: Int,
    val hindiSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SystemTelemetry(
    val arcReactorPower: Int = 98,
    val cpuLoad: Int = 18,
    val ramUsageMb: Int = 4120,
    val quantumLinkStatus: String = "OPTIMAL",
    val batteryPercent: Int = 100,
    val subsystemStatus: String = "ALL PROTOCOLS ACTIVE"
)
