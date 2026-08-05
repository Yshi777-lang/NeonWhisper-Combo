package com.neonwhisper.combo.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neonwhisper.combo.data.db.AppDatabase
import com.neonwhisper.combo.data.db.LocalMessageEntity
import com.neonwhisper.combo.data.db.LocalSessionEntity
import com.neonwhisper.combo.data.api.LlmApiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).chatDao()
    private val apiClient = LlmApiClient()
    
    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun createSession(name: String = "New Chat", systemPrompt: String = "Ты полезный помощник.") {
        viewModelScope.launch {
            val sessionId = UUID.randomUUID().toString()
            val session = LocalSessionEntity(
                id = sessionId,
                name = name,
                systemPrompt = systemPrompt,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            dao.upsertSession(session)
            _currentSessionId.value = sessionId
        }
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            _currentSessionId.value = sessionId
            loadMessages(sessionId)
        }
    }

    private suspend fun loadMessages(sessionId: String) {
        dao.observeMessages(sessionId)
            .map { entities -> 
                entities.map { entity ->
                    ChatMessage(
                        id = entity.id,
                        role = entity.role,
                        content = entity.content,
                        timestamp = entity.createdAt
                    )
                }.sortedBy { it.timestamp }
            }
            .collect { _messages.value = it }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val sessionId = _currentSessionId.value ?: return@launch
            val model = dao.getActiveModel() ?: run {
                _error.value = "No active model configured. Go to Settings first."
                return@launch
            }
            
            _isSending.value = true
            _error.value = null
            
            try {
                // Сохраняем сообщение пользователя
                val userMessage = LocalMessageEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    role = "user",
                    content = content,
                    model = model.modelId,
                    createdAt = System.currentTimeMillis()
                )
                dao.insertMessage(userMessage)
                
                // Получаем историю сообщений (наш кэш)
                val messageHistory = dao.observeMessages(sessionId)
                    .first()
                    .sortedBy { it.createdAt }
                    .takeLast(model.maxContextMessages)
                    .map { it.role to it.content }
                
                // Получаем системный промпт (нашу "Душу")
                val session = dao.getById(sessionId)
                val systemPrompt = session?.systemPrompt ?: "Ты полезный помощник."
                
                // Отправляем запрос к API
                val response = apiClient.sendMessage(
                    modelConfig = model,
                    messages = messageHistory,
                    systemPrompt = systemPrompt
                )
                
                // Сохраняем ответ ассистента
                val assistantMessage = LocalMessageEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    role = "assistant",
                    content = response,
                    model = model.modelId,
                    createdAt = System.currentTimeMillis()
                )
                dao.insertMessage(assistantMessage)
                
                // Умный кэш: обрезаем старые сообщения
                dao.trimMessages(sessionId, model.maxContextMessages)
                
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isSending.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
