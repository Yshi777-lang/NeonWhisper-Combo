package com.neonwhisper.combo.data.db

import androidx.room.*

@Entity(tableName = "local_sessions")
data class LocalSessionEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "system_prompt") val systemPrompt: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis(),
    val pinned: Boolean = false,
    val favorite: Boolean = false
)

@Entity(tableName = "local_messages", foreignKeys = [
    ForeignKey(
        entity = LocalSessionEntity::class,
        parentColumns = ["id"],
        childColumns = ["session_id"],
        onDelete = ForeignKey.CASCADE
    )
], indices = [Index("session_id")])  ← ДОБАВИЛИ ИНДЕКС!
data class LocalMessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "session_id") val sessionId: String,
    val role: String,
    val content: String,
    val model: String? = null,
    @ColumnInfo(name = "input_tokens") val inputTokens: Int? = null,
    @ColumnInfo(name = "output_tokens") val outputTokens: Int? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_ai_models")
data class LocalAiModelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val provider: String,
    @ColumnInfo(name = "api_key") val apiKey: String,
    @ColumnInfo(name = "base_url") val baseUrl: String,
    val modelId: String,
    val active: Boolean = true,
    @ColumnInfo(name = "max_context_messages") val maxContextMessages: Int = 40
)
