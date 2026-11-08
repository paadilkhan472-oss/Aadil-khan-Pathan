package com.example

import com.example.data.AiRepository
import com.example.model.ActionType
import com.example.model.AppLanguage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    private val repository = AiRepository()

    @Test
    fun testWhatsAppIntentExtraction() {
        val query = "Send whatsapp message to +919876543210 saying Meeting at Stark Tower"
        val action = repository.parseActionIntent(query)

        assertNotNull(action)
        assertEquals(ActionType.WHATSAPP, action?.type)
        assertEquals("+919876543210", action?.payload)
        assertTrue(action?.details?.contains("Meeting at Stark Tower") == true)
    }

    @Test
    fun testPhoneCallIntentExtraction() {
        val query = "Call 9112233445"
        val action = repository.parseActionIntent(query)

        assertNotNull(action)
        assertEquals(ActionType.PHONE_CALL, action?.type)
        assertEquals("9112233445", action?.payload)
    }

    @Test
    fun testAppLaunchIntentExtraction() {
        val query = "Open YouTube"
        val action = repository.parseActionIntent(query)

        assertNotNull(action)
        assertEquals(ActionType.LAUNCH_APP, action?.type)
        assertEquals("YouTube", action?.payload)
    }

    @Test
    fun testHindiConversationResponse() = runBlocking {
        val query = "नमस्ते विज़न, तुम कौन हो?"
        val response = repository.processQuery(query, AppLanguage.HINDI)

        assertEquals("hi", response.language)
        assertTrue(response.replyText.contains("विज़न एआई") || response.replyText.contains("स्टार्क"))
    }
}
