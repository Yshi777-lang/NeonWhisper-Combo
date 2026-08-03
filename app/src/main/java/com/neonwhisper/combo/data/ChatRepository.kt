package com.neonwhisper.combo.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.apiStore by preferencesDataStore(name = "api_keys")

class ChatRepository(private val context: Context) {
    suspend fun loadApiKeys() {
        val prefs = context.apiStore.data.first()
        // Загружаем ключи из DataStore
    }

    suspend fun saveApiKey(provider: String, key: String) {
        context.apiStore.edit { prefs ->
            prefs[stringPreferencesKey("api_key_$provider")] = key
        }
    }

    suspend fun getApiKey(provider: String): String? {
        return context.apiStore.data.first()[stringPreferencesKey("api_key_$provider")]
    }
}
