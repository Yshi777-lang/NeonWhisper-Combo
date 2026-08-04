package com.neonwhisper.combo.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neonwhisper.combo.data.db.AppDatabase
import com.neonwhisper.combo.data.db.LocalAiModelEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ProviderSettings(
    val provider: String = "qwen",
    val apiKey: String = "",
    val modelId: String = "qwen-max",
    val baseUrl: String = "https://dashscope-intl.aliyuncs.com/api/v1",
    val maxContextMessages: Int = 40
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).chatDao()
    
    private val _settings = MutableStateFlow(ProviderSettings())
    val settings: StateFlow<ProviderSettings> = _settings.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val activeModel = dao.getActiveModel()
            if (activeModel != null) {
                _settings.value = ProviderSettings(
                    provider = activeModel.provider,
                    apiKey = activeModel.apiKey,
                    modelId = activeModel.modelId,
                    baseUrl = activeModel.baseUrl,
                    maxContextMessages = activeModel.maxContextMessages
                )
            }
        }
    }

    fun updateSetting(update: (ProviderSettings) -> ProviderSettings) {
        _settings.value = update(_settings.value)
    }

    fun saveSettings() {
        viewModelScope.launch {
            _isSaving.value = true
            val current = _settings.value
            val entity = LocalAiModelEntity(
                id = "active_model",
                name = if (current.provider == "qwen") "Qwen AI" else "DeepSeek",
                provider = current.provider,
                apiKey = current.apiKey,
                baseUrl = current.baseUrl,
                modelId = current.modelId,
                active = true,
                maxContextMessages = current.maxContextMessages
            )
            dao.upsertModel(entity)
            _isSaving.value = false
        }
    }
}
