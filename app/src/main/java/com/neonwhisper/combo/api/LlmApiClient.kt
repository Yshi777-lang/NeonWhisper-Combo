package com.neonwhisper.combo.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LlmApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(provider: String, message: String, soulPrompt: String): String = withContext(Dispatchers.IO) {
        when (provider) {
            "Qwen (Alibaba)" -> callQwen(message, soulPrompt)
            "OpenAI" -> callOpenAI(message, soulPrompt)
            "Gemini" -> callGemini(message, soulPrompt)
            "Anthropic" -> callAnthropic(message, soulPrompt)
            else -> throw Exception("Unknown provider: $provider")
        }
    }

    private suspend fun callQwen(message: String, soulPrompt: String): String {
        val url = "https://dashscope-intl.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
        val apiKey = getApiKey("qwen") ?: throw Exception("Qwen API key not set")

        val json = JSONObject()
        json.put("model", "qwen-turbo")
        json.put("input", JSONObject().apply {
            put("messages", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", soulPrompt.ifEmpty { "You are a helpful assistant" })
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", message)
                })
            })
        })

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Qwen API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            return@withContext parseQwenResponse(responseBody)
        }
    }

    private suspend fun callOpenAI(message: String, soulPrompt: String): String {
        val url = "https://api.openai.com/v1/chat/completions"
        val apiKey = getApiKey("openai") ?: throw Exception("OpenAI API key not set")

        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo")
        json.put("messages", org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", soulPrompt.ifEmpty { "You are a helpful assistant" })
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", message)
            })
        })

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("OpenAI API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            return@withContext parseOpenAIResponse(responseBody)
        }
    }

    private suspend fun callGemini(message: String, soulPrompt: String): String {
        val apiKey = getApiKey("gemini") ?: throw Exception("Gemini API key not set")
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val json = JSONObject()
        json.put("contents", org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("parts", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "$soulPrompt\n\nUser: $message")
                    })
                })
            })
        })

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Gemini API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            return@withContext parseGeminiResponse(responseBody)
        }
    }

    private suspend fun callAnthropic(message: String, soulPrompt: String): String {
        val apiKey = getApiKey("anthropic") ?: throw Exception("Anthropic API key not set")
        val url = "https://api.anthropic.com/v1/messages"

        val json = JSONObject()
        json.put("model", "claude-3-haiku-20240307")
        json.put("max_tokens", 1024)
        json.put("system", soulPrompt.ifEmpty { "You are a helpful assistant" })
        json.put("messages", org.json.JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", message)
            })
        })

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Anthropic API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            return@withContext parseAnthropicResponse(responseBody)
        }
    }

    private fun parseQwenResponse(json: String): String {
        val obj = JSONObject(json)
        return obj.getJSONObject("output").getJSONObject("choices")
            .getJSONArray("messages").getJSONObject(0).getString("content")
    }

    private fun parseOpenAIResponse(json: String): String {
        val obj = JSONObject(json)
        return obj.getJSONArray("choices").getJSONObject(0)
            .getJSONObject("message").getString("content")
    }

    private fun parseGeminiResponse(json: String): String {
        val obj = JSONObject(json)
        return obj.getJSONArray("candidates").getJSONObject(0)
            .getJSONObject("content").getJSONArray("parts").getString(0)
    }

    private fun parseAnthropicResponse(json: String): String {
        val obj = JSONObject(json)
        return obj.getJSONArray("content").getJSONObject(0).getString("text")
    }

    private suspend fun getApiKey(provider: String): String? {
        // В реальной реализации нужно читать из DataStore
        return when (provider) {
            "qwen" -> null // Заглушка
            "openai" -> null
            "gemini" -> null
            "anthropic" -> null
            else -> null
        }
    }
}
