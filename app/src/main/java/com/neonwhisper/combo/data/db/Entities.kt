package com.neonwhisper.combo.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "local_sessions")
data class LocalSessionEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "system_prompt") val systemPrompt: String, // Наша "Душа"
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    val pinned: Boolean = false,
    val favorite: Boolean = false
)

@Entity(tableName = "local_messages", foreignKeys = [
    androidx.room.ForeignKey(
        entity = LocalSessionEntity::class,
        parentColumns = ["id"],
        childColumns = ["session_id"],
        onDelete = androidx.room.ForeignKey.CASCADE
    )
])
data class LocalMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_id") val sessionId: String,
    val role: String, // "user" или "assistant"
    val content: String,
    val model: String? = null,
    @ColumnInfo(name = "input_tokens") val inputTokens: Int? = null,
    @ColumnInfo(name = "output_tokens") val outputTokens: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_ai_models")
data class LocalAiModelEntity(
    @PrimaryKey val id: String,
    val name: String, // Например: "Qwen 3.8-Max"
    val provider: String, // "qwen" или "deepseek"
    @ColumnInfo(name = "api_key") val apiKey: String,
    @ColumnInfo(name = "base_url") val baseUrl: String,
    val modelId: String, // Например: "qwen-max" или "deepseek-chat"
    val active: Boolean = true,
    @ColumnInfo(name = "max_context_messages") val maxContextMessages: Int = 40 // Наш кэш!
)
