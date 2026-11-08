package com.example.data

import android.net.Uri
import com.example.model.ActionConfirmation
import com.example.model.ActionType
import com.example.model.AppLanguage
import com.example.model.VisionScanResult
import kotlinx.coroutines.delay
import java.util.regex.Pattern

data class AiResponse(
    val replyText: String,
    val suggestedAction: ActionConfirmation? = null,
    val language: String = "en",
    val sourceNote: String = "Vision AI Stark Core (Enable Firebase in Settings for live Cloud Gemini API)"
)

class AiRepository {

    suspend fun processQuery(
        prompt: String,
        languagePreference: AppLanguage,
        imageUri: Uri? = null
    ): AiResponse {
        // Simulate neural processing latency for realistic Stark HUD feel
        delay(400)

        val cleanPrompt = prompt.trim()
        val isHindi = languagePreference == AppLanguage.HINDI ||
                cleanPrompt.any { it in '\u0900'..'\u097F' } ||
                cleanPrompt.lowercase().contains("kholo") ||
                cleanPrompt.lowercase().contains("karo") ||
                cleanPrompt.lowercase().contains("kaise") ||
                cleanPrompt.lowercase().contains("batao")

        // 1. Detect Action Intents (WhatsApp, Phone Call, Launch App)
        val action = parseActionIntent(cleanPrompt)

        if (action != null) {
            val reply = when (action.type) {
                ActionType.WHATSAPP -> {
                    if (isHindi) {
                        "व्हाट्सएप ट्रांसमिशन प्रोटोकॉल तैयार है। कृपया पुष्टि करें कि क्या आप '${action.target}' को यह संदेश भेजना चाहते हैं।"
                    } else {
                        "WhatsApp transmission protocol prepared for ${action.target}. Please confirm authorization on the Stark HUD to proceed."
                    }
                }
                ActionType.PHONE_CALL -> {
                    if (isHindi) {
                        "वॉयस अपलिंक तैयार किया गया है। '${action.target}' को कॉल करने के लिए पुष्टि करें।"
                    } else {
                        "Voice uplink established for ${action.target}. Awaiting your confirmation to initiate call sequence."
                    }
                }
                ActionType.LAUNCH_APP -> {
                    if (isHindi) {
                        "एप्लिकेशन '${action.title}' खोलने का आदेश प्राप्त हुआ। कृपया स्क्रीन पर पुष्टि करें।"
                    } else {
                        "Initiating application launch sequence for '${action.title}'. Confirmation prompted on your HUD."
                    }
                }
            }
            return AiResponse(
                replyText = reply,
                suggestedAction = action,
                language = if (isHindi) "hi" else "en"
            )
        }

        // 2. Multimodal Image Query Handling
        if (imageUri != null) {
            val reply = if (isHindi) {
                "ऑप्टिकल सेंसर डेटा प्राप्त हुआ। स्टार्क विज़न एल्गोरिदम द्वारा इमेज का विश्लेषण पूरा कर लिया गया है। वस्तु, दृश्य और सामरिक संरचना का आकलन स्क्रीन पर उपलब्ध है।"
            } else {
                "Visual telemetry ingested. Spectral analysis complete. Stark Vision optics identify key structural elements and tactical points of interest."
            }
            return AiResponse(
                replyText = reply,
                language = if (isHindi) "hi" else "en"
            )
        }

        // 3. Conversational JARVIS / ChatGPT-like Response Engine
        val lower = cleanPrompt.lowercase()
        val reply = when {
            // Identity / Greetings
            lower.contains("who are you") || lower.contains("kaun ho") || lower.contains("tum kaun ho") -> {
                if (isHindi) {
                    "मैं विज़न एआई (स्टार्क एडिशन) हूँ। मैं आपका व्यक्तिगत ऑटोनॉमस इंटेलिजेंस असिस्टेंट हूँ, जो वॉयस कमांड, हिंदी-अंग्रेजी संवाद, इमेज रिकॉग्निशन, ऐप लॉन्च और सिस्टम टूल्स से लैस है। मैं आपकी क्या मदद करूँ, सर?"
                } else {
                    "I am VISION AI (Stark Edition) — your personal tactical artificial intelligence. I am equipped for natural voice conversations in English and Hindi, multimodal optical diagnostics, WhatsApp messaging, phone calls, and device application orchestration. Ready for your directive, sir."
                }
            }
            lower.contains("hello") || lower.contains("jarvis") || lower.contains("vision") || lower.contains("namaste") || lower.contains("नमस्ते") || lower.contains("hi") -> {
                if (isHindi) {
                    "नमस्ते सर! स्टार्क क्वांटम कोर 100% ऑनलाइन है। सभी सबसिस्टम सामान्य हैं। आज आपके लिए क्या आदेश है?"
                } else {
                    "Greetings, sir. Quantum core is running at optimal efficiency. Arc reactor output is stable at 98%. How may I assist you today?"
                }
            }
            lower.contains("how are you") || lower.contains("kaise ho") || lower.contains("haal") -> {
                if (isHindi) {
                    "मैं पूरी तरह से सक्रिय और तत्पर हूँ, सर। सभी आंतरिक डायग्नोस्टिक्स ग्रीन ज़ोन में हैं। आप कैसा महसूस कर रहे हैं?"
                } else {
                    "All diagnostic routines report nominal status, sir. Processing nodes are operating at peak throughput. How can I facilitate your mission today?"
                }
            }
            lower.contains("stark") || lower.contains("iron man") || lower.contains("tony") -> {
                if (isHindi) {
                    "स्टार्क इंडस्ट्रीज के मार्क L (Mark 50) नैनोटेक और जार्विस आर्किटेक्चर पर आधारित, यह विज़न एआई आपको अत्याधुनिक असिस्टेंट अनुभव प्रदान करता है।"
                } else {
                    "Engineered with inspiration from Tony Stark's Mark L nanotech architecture and JARVIS cognitive heuristics. Ready to execute tactical commands, search archives, or control communications."
                }
            }
            lower.contains("help") || lower.contains("features") || lower.contains("kya kar sakte ho") || lower.contains("what can you do") -> {
                if (isHindi) {
                    "विज़न एआई की मुख्य क्षमताएं:\n1. 🎙️ हिंदी और अंग्रेजी वॉयस बातचीत\n2. 👁️ विज़न स्कैनर (इमेज और कैमरा का विश्लेषण)\n3. 💬 व्हाट्सएप मैसेज भेजना (पुष्टि के साथ)\n4. 📞 सीधे फोन कॉल लगाना (पुष्टि के साथ)\n5. 🚀 कोई भी एंड्रॉइड ऐप खोलना (उदा. 'Open YouTube')\n6. 📊 लाइव स्टार्क टेलीमेट्री और डायग्नोस्टिक्स"
                } else {
                    "Vision AI Stark Capabilities:\n1. 🎙️ Natural Voice Conversations (English & Hindi)\n2. 👁️ Tactical Optical Scanner (Image & Camera analysis)\n3. 💬 WhatsApp Messaging (with confirmation protocol)\n4. 📞 Voice Uplink / Phone Dialing (with confirmation)\n5. 🚀 Android App Launching (e.g., 'Open YouTube', 'Launch Chrome')\n6. 📊 Real-time Stark Reactor & Core Telemetry Diagnostics"
                }
            }
            lower.contains("status") || lower.contains("telemetry") || lower.contains("battery") || lower.contains("power") -> {
                if (isHindi) {
                    "सिस्टम रिपोर्ट: आर्क रिएक्टर पावर 98%, सीपीयू लोड 18%, एन्क्रिप्शन सुरक्षित (AES-256), वॉयस मॉड्यूल एवं ऑप्टिक्स पूरी तरह तैयार हैं।"
                } else {
                    "Diagnostics summary: Arc reactor baseline output at 98%, CPU thermal load nominal, encryption links active. All primary protocols ready for deployment."
                }
            }
            // General Knowledge & Intelligent Chat
            else -> {
                if (isHindi) {
                    "आपके प्रश्न '$cleanPrompt' पर विश्लेषण पूरा हुआ। स्टार्क डेटाबेस के अनुसार सभी आवश्यक प्रक्रियाएं सक्रिय हैं। यदि आप कोई ऐप लॉन्च करना चाहते हैं, मैसेज या कॉल करना चाहते हैं, तो कृपया निर्देश दें।"
                } else {
                    "Query processed: \"$cleanPrompt\". Analysis confirms all parameters within acceptable operational limits. You can issue voice commands to launch apps, send WhatsApp messages, place calls, or analyze photos at any time, sir."
                }
            }
        }

        return AiResponse(
            replyText = reply,
            language = if (isHindi) "hi" else "en"
        )
    }

    fun parseActionIntent(text: String): ActionConfirmation? {
        val lower = text.lowercase()

        // 1. WhatsApp Intent Detection
        // Patterns: "whatsapp to 9876543210 hello", "send whatsapp message to 9876543210 with text meeting at 5", "whatsapp 9876543210 hi"
        if (lower.contains("whatsapp") || lower.contains("व्हाट्सएप")) {
            val phoneMatcher = Pattern.compile("(\\+?[0-9]{8,15})").matcher(text)
            val phone = if (phoneMatcher.find()) phoneMatcher.group(1) ?: "" else ""

            // Extract message payload after phone or keywords
            var message = ""
            val msgIndex = when {
                lower.indexOf("message") != -1 -> lower.indexOf("message") + 7
                lower.indexOf("saying") != -1 -> lower.indexOf("saying") + 6
                lower.indexOf("text") != -1 -> lower.indexOf("text") + 4
                lower.indexOf("msg") != -1 -> lower.indexOf("msg") + 3
                phone.isNotEmpty() -> text.indexOf(phone) + phone.length
                else -> -1
            }

            if (msgIndex in 0 until text.length) {
                message = text.substring(msgIndex).trim().removePrefix(":").removePrefix("that").removePrefix("with").trim()
            }
            if (message.isBlank()) {
                message = "Hello from Vision AI Stark Edition!"
            }

            return ActionConfirmation(
                type = ActionType.WHATSAPP,
                title = "WhatsApp Transmission",
                target = if (phone.isNotEmpty()) phone else "Direct Recipient",
                details = message,
                payload = phone
            )
        }

        // 2. Phone Call Intent Detection
        // Patterns: "call 9876543210", "dial 9876543210", "phone call 987...", "कॉल करो 9876543210"
        if (lower.startsWith("call") || lower.startsWith("dial") || lower.contains("कॉल") || lower.contains("phone lagao")) {
            val phoneMatcher = Pattern.compile("(\\+?[0-9]{3,15})").matcher(text)
            val phone = if (phoneMatcher.find()) phoneMatcher.group(1) ?: "" else ""

            if (phone.isNotEmpty()) {
                return ActionConfirmation(
                    type = ActionType.PHONE_CALL,
                    title = "Initiate Phone Call",
                    target = phone,
                    details = "Establish cellular voice uplink to $phone",
                    payload = phone
                )
            }
        }

        // 3. Launch App Intent Detection
        // Patterns: "open youtube", "launch chrome", "calculator kholo", "open camera", "open whatsapp"
        val launchKeywords = listOf("open ", "launch ", "start ", "kholo ", "chalao ")
        for (kw in launchKeywords) {
            val idx = lower.indexOf(kw)
            if (idx != -1) {
                val appQuery = text.substring(idx + kw.length).trim().removeSuffix("app").removeSuffix("application").trim()
                if (appQuery.isNotEmpty()) {
                    return ActionConfirmation(
                        type = ActionType.LAUNCH_APP,
                        title = "Launch Application",
                        target = appQuery,
                        details = "Execute system intent to open $appQuery",
                        payload = appQuery
                    )
                }
            }
        }
        if (lower.endsWith("kholo") || lower.endsWith("kholiye")) {
            val appQuery = text.replace("kholo", "").replace("kholiye", "").trim()
            if (appQuery.isNotEmpty()) {
                return ActionConfirmation(
                    type = ActionType.LAUNCH_APP,
                    title = "Launch Application",
                    target = appQuery,
                    details = "Execute system intent to open $appQuery",
                    payload = appQuery
                )
            }
        }

        return null
    }

    suspend fun analyzeImage(uri: Uri): VisionScanResult {
        delay(600) // Simulated scan processing

        val scanPresets = listOf(
            VisionScanResult(
                title = "Tactical Environment & Object Scan",
                summary = "High-density optical telemetry analysis completed. Primary subject captured with high contrast and geometric edge clarity.",
                detectedObjects = listOf("High-Definition Surface", "Geometrical Contours", "Ambient Photons", "Object Mass"),
                tacticalAssessment = "Security level: NOMINAL. No immediate environmental threats detected. Surface integrity 99.4%.",
                confidenceScore = 96,
                hindiSummary = "ऑप्टिकल स्कैन पूर्ण: वस्तु एवं पृष्ठभूमि की संरचना स्पष्ट है। कोई खतरा या विसंगति नहीं पाई गई।"
            ),
            VisionScanResult(
                title = "Spectral Hardware & Interface Diagnostic",
                summary = "Advanced edge-detection and character pattern recognition active. Visual field rendered across 4 primary optical quadrants.",
                detectedObjects = listOf("Visual Terminal", "Component Symmetry", "Reflective Geometry", "Optic Matrix"),
                tacticalAssessment = "Optimal alignment detected. Stark Neural Network assigns 98% validity to captured telemetry.",
                confidenceScore = 98,
                hindiSummary = "सामरिक निदान: ऑब्जेक्ट और इंटरफ़ेस की स्थिति सामान्य और संतुलित है।"
            )
        )

        return scanPresets.random()
    }
}
