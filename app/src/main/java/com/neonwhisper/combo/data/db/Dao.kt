package com.neonwhisper.combo.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // --- СЕССИИ ---
    @Query("SELECT * FROM local_sessions ORDER BY updated_at DESC")
    fun observeSessions(): Flow<List<LocalSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSession(session: LocalSessionEntity)

    // --- СООБЩЕНИЯ ---
    @Query("SELECT * FROM local_messages WHERE session_id = :sessionId ORDER BY created_at ASC")
    fun observeMessages(sessionId: String): Flow<List<LocalMessageEntity>>

    @Insert
    suspend fun insertMessage(message: LocalMessageEntity)

    // УМНЫЙ КЭШ: Оставляем только последние N сообщений
    @Query("""
        DELETE FROM local_messages 
        WHERE session_id = :sessionId 
        AND id NOT IN (
            SELECT id FROM local_messages 
            WHERE session_id = :sessionId 
            ORDER BY created_at DESC 
            LIMIT :keepCount
        )
    """)
    suspend fun trimMessages(sessionId: String, keepCount: Int)

    // --- МОДЕЛИ ---
    @Query("SELECT * FROM local_ai_models WHERE active = 1 LIMIT 1")
    suspend fun getActiveModel(): LocalAiModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModel(model: LocalAiModelEntity)
}
