package com.neonwhisper.combo.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class LlmApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private var qwenKey: String = ""
    private var openaiKey: String = ""
    private var geminiKey: String = ""
    private var anthropicKey: String = ""

    fun setApiKey(qwen: String, openai: String, gemini: String, anthropic: String) {
        qwenKey = qwen
        openaiKey = openai
        geminiKey = gemini
        anthropicKey = anthropic
    }

    suspend fun sendMessage(provider: String, message: String, soulPrompt: String): String {
        return sendMessageWithModel(provider, getDefaultModel(provider), message, soulPrompt)
    }

    suspend fun sendMessageWithModel(provider: String, model: String, message: String, soulPrompt: String): String {
        return withContext(Dispatchers.IO) {
            when (provider) {
                "Qwen (Alibaba)" -> callQwen(model, message, soulPrompt)
                "OpenAI" -> callOpenAI(model, message, soulPrompt)
                "Gemini" -> callGemini(model, message, soulPrompt)
                "Anthropic" -> callAnthropic(model, message, soulPrompt)
                else -> throw Exception("Unknown provider: $provider")
            }
        }
    }

    private fun getDefaultModel(provider: String): String {
        return when(provider) {
            "Qwen (Alibaba)" -> "qwen-turbo"
            "OpenAI" -> "gpt-3.5-turbo"
            "Gemini" -> "gemini-1.5-flash"
            "Anthropic" -> "claude-3-haiku-20240307"
            else -> "qwen-turbo"
        }
    }

    private fun callQwen(model: String, message: String, soulPrompt: String): String {
        if (qwenKey.isEmpty()) throw Exception("Qwen API key not set")
        val url = "https://dashscope-intl.aliyuncs.com/api/v1/services/aigc/text-generation/generation"

        val messagesArray = JSONArray()
        messagesArray.put(JSONObject().apply {
            put("role", "system")
            put("content", soulPrompt.ifEmpty { "You are a helpful assistant" })
        })
        messagesArray.put(JSONObject().apply {
            put("role", "user")
            put("content", message)
        })

        val json = JSONObject()
        json.put("model", model)
        json.put("input", JSONObject().apply {
            put("messages", messagesArray)
        })

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $qwenKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Qwen API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            val obj = JSONObject(responseBody)
            return obj.getJSONObject("output")
                .getJSONArray("choices").getJSONObject(0)
                .getString("message")
        }
    }

    private fun callOpenAI(model: String, message: String, soulPrompt: String): String {
        if (openaiKey.isEmpty()) throw Exception("OpenAI API key not set")
        val url = "https://api.openai.com/v1/chat/completions"

        val messagesArray = JSONArray()
        messagesArray.put(JSONObject().apply {
            put("role", "system")
            put("content", soulPrompt.ifEmpty { "You are a helpful assistant" })
        })
        messagesArray.put(JSONObject().apply {
            put("role", "user")
            put("content", message)
        })

        val json = JSONObject()
        json.put("model", model)
        json.put("messages", messagesArray)

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $openaiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("OpenAI API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            val obj = JSONObject(responseBody)
            return obj.getJSONArray("choices").getJSONObject(0)
                .getJSONObject("message").getString("content")
        }
    }

    private fun callGemini(model: String, message: String, soulPrompt: String): String {
        if (geminiKey.isEmpty()) throw Exception("Gemini API key not set")
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$geminiKey"

        val json = JSONObject()
        json.put("contents", JSONArray().apply {
            put(JSONObject().apply {
                put("parts", JSONArray().apply {
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
            val obj = JSONObject(responseBody)
            return obj.getJSONArray("candidates").getJSONObject(0)
                .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
        }
    }

    private fun callAnthropic(model: String, message: String, soulPrompt: String): String {
        if (anthropicKey.isEmpty()) throw Exception("Anthropic API key not set")
        val url = "https://api.anthropic.com/v1/messages"

        val json = JSONObject()
        json.put("model", model)
        json.put("max_tokens", 1024)
        json.put("system", soulPrompt.ifEmpty { "You are a helpful assistant" })
        json.put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", message)
            })
        })

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("x-api-key", anthropicKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Anthropic API error: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            val obj = JSONObject(responseBody)
            return obj.getJSONArray("content").getJSONObject(0).getString("text")
        }
    }
}
