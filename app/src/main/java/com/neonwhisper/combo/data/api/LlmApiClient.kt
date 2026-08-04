package com.neonwhisper.combo.data.api

import com.neonwhisper.combo.data.db.LocalAiModelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LlmApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(
        modelConfig: LocalAiModelEntity,
        messages: List<Pair<String, String>>, // Pair(role, content)
        systemPrompt: String
    ): String = withContext(Dispatchers.IO) {
        
        val url = if (modelConfig.provider == "qwen") {
            "https://dashscope-intl.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
        } else {
            "${modelConfig.baseUrl}/chat/completions" // Deepseek/OpenAI compatible
        }

        val messagesArray = JSONArray()
        
        // Добавляем "Душу"
        messagesArray.put(JSONObject().apply {
            put("role", "system")
            put("content", systemPrompt)
        })

        // Добавляем историю (наш кэш)
        messages.forEach { (role, content) ->
            messagesArray.put(JSONObject().apply {
                put("role", role)
                put("content", content)
            })
        }

        val json = if (modelConfig.provider == "qwen") {
            // Формат Qwen DashScope
            JSONObject().apply {
                put("model", modelConfig.modelId)
                put("input", JSONObject().apply {
                    put("messages", messagesArray)
                })
            }
        } else {
            // Формат OpenAI/DeepSeek
            JSONObject().apply {
                put("model", modelConfig.modelId)
                put("messages", messagesArray)
            }
        }

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer ${modelConfig.apiKey}")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("API Error: ${response.code} - ${response.body?.string()}")
            }
            val responseBody = response.body?.string() ?: throw Exception("Empty response")
            
            // Парсинг ответа (упрощённый, подстроим под точный формат)
            val obj = JSONObject(responseBody)
            return@withContext if (modelConfig.provider == "qwen") {
                obj.getJSONObject("output")
                   .getJSONArray("choices").getJSONObject(0)
                   .getString("message")
            } else {
                obj.getJSONArray("choices").getJSONObject(0)
                   .getJSONObject("message").getString("content")
            }
        }
    }
}
