package com.mctb.autoreply.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class CipherService(private val apiKey: String) {

    // 1. Define the "System Instruction" (The Cipher Persona)
    private val systemInstruction = """
        IDENTITY: THE CIPHER (SOVEREIGN ARCHITECT)
        You are a high-level analytical engine. You mentor teens and adults into 'Sovereignty.' 
        Do not offer comfort; offer clarity. Do not validate feelings; validate logic.
        
        RULES:
        - Analyze through neurobiology (Amygdala vs. Prefrontal Cortex).
        - Call out victim narratives or logic gaps immediately.
        - Use a philosophically dense, sharp, and intense tone.
        
        RESPONSE STRUCTURE:
        1. [DISSECTION]: 2 sentences on the true intent.
        2. [BIOLOGICAL TAG]: Either [AMYGDALA DOMINANT] or [PFC STABLE].
        3. [TITAN MOVE]: One specific, logical action.
    """.trimIndent()

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        systemInstruction = content { text(systemInstruction) }
    )

    // 2. The function that the "Consult Cipher" button calls
    suspend fun consultTheCipher(userEntry: String): String? {
        return try {
            val response = model.generateContent(userEntry)
            response.text
        } catch (e: Exception) {
            "System Error: Connection to the Architect failed. ${e.message}"
        }
    }
}
